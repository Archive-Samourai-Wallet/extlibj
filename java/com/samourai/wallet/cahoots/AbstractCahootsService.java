package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.TxBroadcastInteraction;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.RandomUtil;
import com.samourai.wallet.whirlpool.WhirlpoolConst;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCahootsService<T extends Cahoots> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsService.class);

    private BipFormatSupplier bipFormatSupplier;
    protected NetworkParameters params;
    private TypeInteraction typeInteractionBroadcast;

    public AbstractCahootsService(BipFormatSupplier bipFormatSupplier, NetworkParameters params, TypeInteraction typeInteractionBroadcast) {
        this.bipFormatSupplier = bipFormatSupplier;
        this.params = params;
        this.typeInteractionBroadcast = typeInteractionBroadcast;
    }

    public abstract T startInitiator(CahootsWallet cahootsWallet, int account, CahootsContext cahootsContext) throws Exception;

    public abstract T startCollaborator(CahootsWallet cahootsWallet, int account, T payload0) throws Exception;

    public abstract T reply(CahootsWallet cahootsWallet, T payload) throws Exception;

    public void verifyResponse(CahootsContext cahootsContext, T response, T request) throws Exception {
        if (request != null) {
            // properties should never change
            if (response.getType() != request.getType()) {
                throw new Exception("Invalid altered Cahoots type");
            }
            if (response.getVersion() != request.getVersion()) {
                throw new Exception("Invalid altered Cahoots version");
            }
            if (!response.getParams().equals(request.getParams())) {
                throw new Exception("Invalid altered Cahoots params");
            }

            // step should increment
            if (response.getStep() != request.getStep() + 1) {
                throw new Exception("Invalid response step");
            }
        }
    }

    public SorobanInteraction checkInteraction(ManualCahootsMessage request, Cahoots cahootsResponse) {
        // broadcast by SENDER
        if (request.getTypeUser().getPartner().equals(typeInteractionBroadcast.getTypeUser()) && (request.getStep()+1) == typeInteractionBroadcast.getStep()) {
            return new TxBroadcastInteraction(typeInteractionBroadcast, cahootsResponse);
        }
        return null;
    }

    protected HashMap<String, ECKey> computeKeyBag(Cahoots2x cahoots, List<CahootsUtxo> utxos) {
        // utxos by hash
        HashMap<String, CahootsUtxo> utxosByHash = new HashMap<String, CahootsUtxo>();
        for (CahootsUtxo utxo : utxos) {
            MyTransactionOutPoint outpoint = utxo.getOutpoint();
            utxosByHash.put(outpoint.getHash().toString() + "-" + outpoint.getIndex(), utxo);
        }

        Transaction transaction = cahoots.getTransaction();
        HashMap<String, ECKey> keyBag = new HashMap<String, ECKey>();
        for (TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();
            String key = outpoint.getHash().toString() + "-" + outpoint.getIndex();
            if (utxosByHash.containsKey(key)) {
                CahootsUtxo utxo = utxosByHash.get(key);
                ECKey eckey = utxo.getKey();
                keyBag.put(outpoint.toString(), eckey);
            }
        }
        return keyBag;
    }

    // verify

    protected long computeSpendAmount(HashMap<String,ECKey> keyBag, CahootsWallet cahootsWallet, Cahoots2x cahoots, CahootsTypeUser typeUser) throws Exception {
        long spendAmount = 0;

        if (log.isDebugEnabled()) {
            log.debug("computeSpendAmount: keyBag="+keyBag.keySet());
        }
        Transaction transaction = cahoots.getTransaction();
        for(TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();
            if (keyBag.containsKey(outpoint.toString())) {
                Long inputValue = cahoots.getOutpoints().get(outpoint.getHash().toString() + "-" + outpoint.getIndex());
                if (inputValue != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("computeSpendAmount: +input "+inputValue + " "+outpoint.toString());
                    }
                    spendAmount += inputValue;
                }
            }
        }

        int myAccount = typeUser.equals(CahootsTypeUser.SENDER) ? cahoots.getAccount() : cahoots.getCounterpartyAccount();
        List<String> myOutputAddresses = computeMyOutputAddresses(cahootsWallet, myAccount);

        for(TransactionOutput output : transaction.getOutputs()) {
            String outputAddress = bipFormatSupplier.getToAddress(output);
            if (outputAddress != null && myOutputAddresses.contains(outputAddress)) {
                if (output.getValue() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("computeSpendAmount: -output " + output.getValue().longValue()+" "+outputAddress);
                    }
                    spendAmount -= output.getValue().longValue();
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("computeSpendAmount = " + spendAmount+" (account="+myAccount+")");
        }
        return spendAmount;
    }

    protected List<String> computeMyOutputAddresses(CahootsWallet cahootsWallet, int myAccount) throws Exception {
        List<String> addresses = new LinkedList<String>();

        // compute change addresses
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(myAccount, false);
        addresses.addAll(computeMyOutputAddresses(cahootsWallet, changeAddress.getHdAddress()));

        // compute receive addresses
        if (myAccount != WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            BipAddress receiveAddress = cahootsWallet.fetchAddressReceive(myAccount, false);
            addresses.addAll(computeMyOutputAddresses(cahootsWallet, receiveAddress.getHdAddress()));
        }
        return addresses;
    }

    private List<String> computeMyOutputAddresses(CahootsWallet cahootsWallet, HD_Address hdAddress) throws Exception {
        int account = hdAddress.getAccountIndex();
        int idx = hdAddress.getAddressIndex();
        int chain = hdAddress.getChainIndex();

        int NB_ADDRESSES_MAX=4;
        idx = Math.max(0, idx-NB_ADDRESSES_MAX); // go back address index for verification

        List<String> addresses = new LinkedList<String>();
        for (int i=0; i<NB_ADDRESSES_MAX; i++) {
            SegwitAddress segwitAddress = cahootsWallet.getBip84Wallet().getSegwitAddressAt(account, chain, idx+i);
            addresses.add(segwitAddress.getBech32AsString());
            if (log.isDebugEnabled()) {
                log.debug("myOutputAddress " + account + ":m/" + chain + "/" + (idx + i) + " = " + segwitAddress.getBech32AsString());
            }
        }
        return addresses;
    }

    protected  _TransactionOutput computeTxOutput(BipAddress bipAddress, long amount) throws Exception{
        return computeTxOutput(bipAddress.getAddressString(), amount);
    }

    protected Triple<byte[], byte[], String> computeOutput(BipAddress bipAddress, byte[] fingerprint) {
        HD_Address hdAddress = bipAddress.getHdAddress();
        return computeOutput(hdAddress.getECKey().getPubKey(), fingerprint, hdAddress.getChainIndex(), hdAddress.getAddressIndex());
    }

    protected  _TransactionOutput computeTxOutput(String receiveAddressString, long amount) throws Exception{
        byte[] scriptPubKey_A0 = Bech32UtilGeneric.getInstance().computeScriptPubKey(receiveAddressString, params);
        return new _TransactionOutput(params, null, Coin.valueOf(amount), scriptPubKey_A0);
    }

    protected Triple<byte[], byte[], String> computeOutput(byte[] pubKey, byte[] fingerprint, int chainIdx, int addressIdx) {
        return Triple.of(pubKey, fingerprint, "M/"+chainIdx+"/" + addressIdx);
    }

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    // overridable for tests
    protected int getRandNextInt(int bound) {
        SecureRandom random = RandomUtil.getSecureRandom();
        return random.nextInt(bound);
    }

    // overridable for tests
    protected void shuffleUtxos(List<CahootsUtxo> utxos) {
        Collections.shuffle(utxos);
    }
}
