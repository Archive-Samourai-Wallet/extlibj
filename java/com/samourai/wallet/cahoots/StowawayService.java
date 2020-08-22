package com.samourai.wallet.cahoots;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.FeeUtil;
import com.samourai.wallet.whirlpool.WhirlpoolConst;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StowawayService extends AbstractCahootsService<Stowaway> {
    private static final Logger log = LoggerFactory.getLogger(StowawayService.class);

    public StowawayService(NetworkParameters params) {
        super(params);
    }

    public Stowaway startInitiator(CahootsWallet cahootsWallet, long amount, int account) {
        byte[] fingerprint = cahootsWallet.getBip84Wallet().getWallet().getFingerprint();
        Stowaway stowaway0 = doStowaway0(amount, account, fingerprint);
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway => step="+stowaway0.getStep());
        }
        return stowaway0;
    }

    @Override
    public Stowaway startCollaborator(Stowaway stowaway0, CahootsWallet cahootsWallet, int account) throws Exception {
        if (account != 0) {
            throw new Exception("Invalid Stowaway collaborator account");
        }
        Stowaway stowaway1 = doStowaway1(stowaway0, cahootsWallet);
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway => step="+stowaway1.getStep());
        }
        return stowaway1;
    }

    @Override
    public Stowaway reply(Stowaway stowaway, CahootsWallet cahootsWallet, long feePerB) throws Exception {
        int step = stowaway.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway <= step="+step);
        }
        Stowaway payload;
        switch (step) {
            case 1:
                payload = doStowaway2(stowaway, cahootsWallet, feePerB);
                break;
            case 2:
                payload = doStowaway3(stowaway, cahootsWallet);
                break;
            case 3:
                payload = doStowaway4(stowaway, cahootsWallet);
                break;
            default:
                throw new Exception("Unrecognized #Cahoots step");
        }
        if (payload == null) {
            throw new Exception("Cannot compose #Cahoots");
        }
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway => step="+payload.getStep());
        }
        return payload;
    }

    //
    // sender
    //
    public Stowaway doStowaway0(long spendAmount, int account, byte[] fingerprint) {
        //
        //
        // step0: B sends spend amount to A,  creates step0
        //
        //
        if (log.isDebugEnabled()) {
            log.debug("sender account (0):" + account);
        }
        Stowaway stowaway0 = new Stowaway(spendAmount, params, account);
        stowaway0.setFingerprint(fingerprint);
        return stowaway0;
    }

    //
    // receiver
    //
    public Stowaway doStowaway1(Stowaway stowaway0, CahootsWallet cahootsWallet) throws Exception {
        BIP84Wallet bip84Wallet = cahootsWallet.getBip84Wallet();

        byte[] fingerprint = bip84Wallet.getWallet().getFingerprint();
        stowaway0.setFingerprintCollab(fingerprint);

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(0);
        // sort in descending order by value
        Collections.sort(utxos, new UTXO.UTXOComparator());
        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        long totalContributedAmount = 0L;
        List<CahootsUtxo> highUTXO = new ArrayList<CahootsUtxo>();
        for (CahootsUtxo utxo : utxos) {
            if (utxo.getValue() > stowaway0.getSpendAmount() + SamouraiWalletConst.bDust.longValue()) {
                highUTXO.add(utxo);
            }
        }
        if(highUTXO.size() > 0)    {
            SecureRandom random = new SecureRandom();
            CahootsUtxo utxo = highUTXO.get(random.nextInt(highUTXO.size()));
            if (log.isDebugEnabled()) {
                log.debug("BIP84 selected random utxo:" + utxo.getValue());
            }
            selectedUTXO.add(utxo);
            totalContributedAmount = utxo.getValue();
        }
        if (selectedUTXO.size() == 0) {
            for (CahootsUtxo utxo : utxos) {
                selectedUTXO.add(utxo);
                totalContributedAmount += utxo.getValue();
                if (log.isDebugEnabled()) {
                    log.debug("BIP84 selected utxo:" + utxo.getValue());
                }
                if (stowaway0.isContributedAmountSufficient(totalContributedAmount)) {
                    break;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalContributedAmount+", requiredAmount="+stowaway0.computeRequiredAmount());
        }
        if (!stowaway0.isContributedAmountSufficient(totalContributedAmount)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        NetworkParameters params = stowaway0.getParams();

        //
        //
        // step1: A utxos -> B (take largest that cover amount)
        //
        //

        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsA = new HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>>();

        for (CahootsUtxo utxo : selectedUTXO) {
            MyTransactionOutPoint _outpoint = utxo.getOutpoint();
            ECKey eckey = utxo.getKey();
            String path = utxo.getPath();
            inputsA.put(_outpoint, Triple.of(eckey.getPubKey(), stowaway0.getFingerprintCollab(), path));
        }

        // destination output
        int idx = bip84Wallet.getWallet().getAccount(0).getReceive().getAddrIdx();
        SegwitAddress segwitAddress = bip84Wallet.getAddressAt(0, idx);
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        byte[] scriptPubKey_A = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress.getBech32AsString(), params);
        _TransactionOutput output_A0 = new _TransactionOutput(params, null, Coin.valueOf(stowaway0.getSpendAmount()), scriptPubKey_A);
        outputsA.put(output_A0, Triple.of(segwitAddress.getECKey().getPubKey(), stowaway0.getFingerprintCollab(), "M/0/" + idx));

        stowaway0.setDestination(segwitAddress.getBech32AsString());

        Stowaway stowaway1 = new Stowaway(stowaway0);
        stowaway1.doStep1(inputsA, outputsA);

        return stowaway1;
    }

    //
    // sender
    //
    public Stowaway doStowaway2(Stowaway stowaway1, CahootsWallet cahootsWallet, long feePerB) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("sender account (2):" + stowaway1.getAccount());
        }

        Transaction transaction = stowaway1.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
        }
        int nbIncomingInputs = transaction.getInputs().size();

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stowaway1.getAccount());
        // sort in ascending order by value
        Collections.sort(utxos, new UTXO.UTXOComparator());
        Collections.reverse(utxos);

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        int nbTotalSelectedOutPoints = 0;
        long totalSelectedAmount = 0L;
        List<CahootsUtxo> lowUTXO = new ArrayList<CahootsUtxo>();
        for (CahootsUtxo utxo : utxos) {
            if(utxo.getValue() < stowaway1.getSpendAmount())    {
                lowUTXO.add(utxo);
            }
        }

        List<List<CahootsUtxo>> listOfLists = new ArrayList<List<CahootsUtxo>>();
        Collections.shuffle(lowUTXO);
        listOfLists.add(lowUTXO);
        listOfLists.add(utxos);
        for(List<CahootsUtxo> list : listOfLists)   {

            selectedUTXO.clear();
            totalSelectedAmount = 0L;
            nbTotalSelectedOutPoints = 0;

            for (CahootsUtxo utxo : list) {
                selectedUTXO.add(utxo);
                totalSelectedAmount += utxo.getValue();
                if (log.isDebugEnabled()) {
                    log.debug("BIP84 selected utxo:" + utxo.getValue());
                }
                nbTotalSelectedOutPoints ++;
                if (stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB))) {

                    // discard "extra" utxo, if any
                    List<CahootsUtxo> _selectedUTXO = new ArrayList<CahootsUtxo>();
                    Collections.reverse(selectedUTXO);
                    int _nbTotalSelectedOutPoints = 0;
                    long _totalSelectedAmount = 0L;
                    for (CahootsUtxo utxoSel : selectedUTXO) {
                        _selectedUTXO.add(utxoSel);
                        _totalSelectedAmount += utxoSel.getValue();
                        if (log.isDebugEnabled()) {
                            log.debug("BIP84 post selected utxo:" + utxoSel.getValue());
                        }
                        _nbTotalSelectedOutPoints ++;
                        if (stowaway1.isContributedAmountSufficient(_totalSelectedAmount, estimatedFee(_nbTotalSelectedOutPoints, nbIncomingInputs, feePerB))) {
                            selectedUTXO.clear();
                            selectedUTXO.addAll(_selectedUTXO);
                            totalSelectedAmount = _totalSelectedAmount;
                            nbTotalSelectedOutPoints = _nbTotalSelectedOutPoints;
                            break;
                        }
                    }

                    break;
                }
            }
            if (stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB))) {
                break;
            }
        }

        long estimatedFee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB);
        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalSelectedAmount+", requiredAmount="+stowaway1.computeRequiredAmount(estimatedFee));
        }
        if (!stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        long fee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB);
        if (log.isDebugEnabled()) {
            log.debug("fee:" + fee);
        }

        NetworkParameters params = stowaway1.getParams();

        //
        //
        // step2: B verif, utxos -> A (take smallest that cover amount)
        //
        //

        BIP84Wallet bip84Wallet = cahootsWallet.getBip84Wallet();
        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsB = new HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>>();

        for (CahootsUtxo utxo : selectedUTXO) {
            MyTransactionOutPoint _outpoint = utxo.getOutpoint();
            ECKey eckey = utxo.getKey();
            String path = utxo.getPath();
            inputsB.put(_outpoint, Triple.of(eckey.getPubKey(), stowaway1.getFingerprint(), path));
        }

        if (log.isDebugEnabled()) {
            log.debug("inputsB:" + inputsB.size());
        }

        // change output
        SegwitAddress segwitAddress;
        int idx;
        if (stowaway1.getAccount() == WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            idx = cahootsWallet.fetchPostChangeIndex();
            HD_Address addr = bip84Wallet.getWallet().getAccountAt(stowaway1.getAccount()).getChange().getAddressAt(idx);
            segwitAddress = new SegwitAddress(addr.getPubKey(), params);
        } else {
            idx = bip84Wallet.getWallet().getAccount(0).getChange().getAddrIdx();
            segwitAddress = bip84Wallet.getAddressAt(1, idx);
        }
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsB = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        byte[] scriptPubKey_B = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress.getBech32AsString(), params);
        _TransactionOutput output_B0 = new _TransactionOutput(params, null, Coin.valueOf((totalSelectedAmount - stowaway1.getSpendAmount()) - fee), scriptPubKey_B);
        outputsB.put(output_B0, Triple.of(segwitAddress.getECKey().getPubKey(), stowaway1.getFingerprint(), "M/1/" + idx));

        if (log.isDebugEnabled()) {
            log.debug("outputsB:" + outputsB.size());
        }

        Stowaway stowaway2 = new Stowaway(stowaway1);
        stowaway2.doStep2(inputsB, outputsB);
        stowaway2.setFeeAmount(fee);

        return stowaway2;
    }

    private long estimatedFee(int nbTotalSelectedOutPoints, int nbIncomingInputs, long feePerB) {
        return FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 2, 0, feePerB);
    }

    //
    // receiver
    //
    public Stowaway doStowaway3(Stowaway stowaway2, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(0);
        HashMap<String, ECKey> keyBag_A = computeKeyBag(stowaway2, utxos);

        Stowaway stowaway3 = new Stowaway(stowaway2);
        stowaway3.doStep3(keyBag_A);

        return stowaway3;
    }

    //
    // sender
    //
    public Stowaway doStowaway4(Stowaway stowaway3, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stowaway3.getAccount());
        HashMap<String, ECKey> keyBag_B = computeKeyBag(stowaway3, utxos);

        Stowaway stowaway4 = new Stowaway(stowaway3);
        stowaway4.doStep4(keyBag_B);

        return stowaway4;
    }

}
