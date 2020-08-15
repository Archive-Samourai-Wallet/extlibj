package com.samourai.wallet.cahoots;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.FeeUtil;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.whirlpool.WhirlpoolConst;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class STONEWALLx2Service {
    private static final Logger log = LoggerFactory.getLogger(STONEWALLx2Service.class);

    private NetworkParameters params;

    public STONEWALLx2Service(NetworkParameters params) {
        this.params = params;
    }

    public STONEWALLx2 startInitiator(CahootsWallet cahootsWallet, long amount, String address, int account) {
        byte[] fingerprint = cahootsWallet.getBip84Wallet().getWallet().getFingerprint();
        STONEWALLx2 stonewall0 = doSTONEWALLx2_0(amount, address, account, fingerprint);
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 => step="+stonewall0.getStep());
        }
        return stonewall0;
    }

    public STONEWALLx2 startCollaborator(STONEWALLx2 stonewall0, CahootsWallet cahootsWallet, int account) throws Exception {
        STONEWALLx2 stonewall1 = doSTONEWALLx2_1(stonewall0, cahootsWallet, account);
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 => step="+stonewall1.getStep());
        }
        return stonewall1;
    }

    public STONEWALLx2 resume(STONEWALLx2 stonewall, CahootsWallet cahootsWallet, long feePerB) throws Exception {
        int step = stonewall.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 <= step="+step);
        }
        STONEWALLx2 payload;
        switch (step) {
            case 1:
                payload = doSTONEWALLx2_2(stonewall, cahootsWallet, feePerB);
                break;
            case 2:
                payload = doSTONEWALLx2_3(stonewall, cahootsWallet);
                break;
            case 3:
                payload = doSTONEWALLx2_4(stonewall, cahootsWallet);
                break;
            default:
                throw new Exception("Unrecognized #Cahoots step");
        }
        if (payload == null) {
            throw new Exception("Cannot compose #Cahoots");
        }
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 => step="+payload.getStep());
        }
        return payload;
    }

    //
    // sender
    //
    public STONEWALLx2 doSTONEWALLx2_0(long spendAmount, String address, int account, byte[] fingerprint) {
        //
        //
        // step0: B sends spend amount to A,  creates step0
        //
        //
        STONEWALLx2 stonewall0 = new STONEWALLx2(spendAmount, address, params, account);
        stonewall0.setFingerprint(fingerprint);

        return stonewall0;
    }



    //
    // counterparty
    //
    public STONEWALLx2 doSTONEWALLx2_1(STONEWALLx2 stonewall0, CahootsWallet cahootsWallet, int account) throws Exception {
        BIP84Wallet bip84Wallet = cahootsWallet.getBip84Wallet();

        stonewall0.setCounterpartyAccount(account);
        byte[] fingerprint = bip84Wallet.getWallet().getFingerprint();
        stonewall0.setFingerprintCollab(fingerprint);

        List<UTXO> utxos = cahootsWallet.getCahootsUTXO(stonewall0.getCounterpartyAccount());
        Collections.shuffle(utxos);

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<UTXO> selectedUTXO = new ArrayList<UTXO>();
        long totalContributedAmount = 0L;
        for (int step = 0; step < 3; step++) {

            if (stonewall0.getCounterpartyAccount() == 0) {
                step = 2;
            }

            List<String> seenTxs = new ArrayList<String>();
            selectedUTXO = new ArrayList<UTXO>();
            totalContributedAmount = 0L;
            for (UTXO utxo : utxos) {

                switch (step) {
                    case 0:
                        if (utxo.getPath() != null && utxo.getPath().length() > 3 && utxo.getPath().charAt(2) != '0') {
                            continue;
                        }
                        break;
                    case 1:
                        if (utxo.getPath() != null && utxo.getPath().length() > 3 && utxo.getPath().charAt(2) != '1') {
                            continue;
                        }
                        break;
                    default:
                        break;
                }

                UTXO _utxo = new UTXO();
                for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                    if (!seenTxs.contains(outpoint.getTxHash().toString())) {
                        _utxo.getOutpoints().add(outpoint);
                        seenTxs.add(outpoint.getTxHash().toString());
                    }
                }

                if (_utxo.getOutpoints().size() > 0) {
                    selectedUTXO.add(_utxo);
                    totalContributedAmount += _utxo.getValue();
                    if (log.isDebugEnabled()) {
                        log.debug("BIP84 selected utxo:" + _utxo.getValue());
                    }
                }

                if (totalContributedAmount > stonewall0.getSpendAmount() + SamouraiWalletConst.bDust.longValue()) {
                    break;
                }
            }
            if (totalContributedAmount > stonewall0.getSpendAmount() + SamouraiWalletConst.bDust.longValue()) {
                break;
            }
        }
        if (!(totalContributedAmount > stonewall0.getSpendAmount() + SamouraiWalletConst.bDust.longValue())) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        if (log.isDebugEnabled()) {
            log.debug("BIP84 selected utxos:" + selectedUTXO.size());
        }

        NetworkParameters params = stonewall0.getParams();

        //
        //
        // step1: A utxos -> B (take largest that cover amount)
        //
        //

        HashMap<_TransactionOutPoint, Triple<byte[], byte[], String>> inputsA = new HashMap<_TransactionOutPoint, Triple<byte[], byte[], String>>();

        for (UTXO utxo : selectedUTXO) {
            for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                _TransactionOutPoint _outpoint = new _TransactionOutPoint(outpoint);

                ECKey eckey = cahootsWallet.getPrivKey(_outpoint.getAddress(), stonewall0.getCounterpartyAccount());
                String path = cahootsWallet.getUnspentPath(_outpoint.getAddress());
                inputsA.put(_outpoint, Triple.of(eckey.getPubKey(), stonewall0.getFingerprintCollab(), path));
            }
        }

        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        if (stonewall0.getCounterpartyAccount() == WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            // contributor mix output
            int idx = cahootsWallet.getHighestPostChangeIdx();
            SegwitAddress segwitAddress0 = bip84Wallet.getAddressAt(stonewall0.getCounterpartyAccount(), 1, idx);
            byte[] scriptPubKey_A0 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress0.getBech32AsString(), params);
            _TransactionOutput output_A0 = new _TransactionOutput(params, null, Coin.valueOf(stonewall0.getSpendAmount()), scriptPubKey_A0);
            outputsA.put(output_A0, Triple.of(segwitAddress0.getECKey().getPubKey(), stonewall0.getFingerprintCollab(), "M/1/" + idx));

            // contributor change output
            ++idx;
            SegwitAddress segwitAddress1 = bip84Wallet.getAddressAt(stonewall0.getCounterpartyAccount(), 1, idx);
            byte[] scriptPubKey_A1 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress1.getBech32AsString(), params);
            _TransactionOutput output_A1 = new _TransactionOutput(params, null, Coin.valueOf(totalContributedAmount - stonewall0.getSpendAmount()), scriptPubKey_A1);
            outputsA.put(output_A1, Triple.of(segwitAddress1.getECKey().getPubKey(), stonewall0.getFingerprintCollab(), "M/1/" + idx));
            stonewall0.setCollabChange(segwitAddress1.getBech32AsString());
        } else {
            // contributor mix output
            int idx = bip84Wallet.getWallet().getAccount(0).getReceive().getAddrIdx();
            SegwitAddress segwitAddress0 = bip84Wallet.getAddressAt(0, 0, idx);
            if (segwitAddress0.getBech32AsString().equalsIgnoreCase(stonewall0.getDestination())) {
                segwitAddress0 = bip84Wallet.getAddressAt(0, 0, idx + 1);
            }
            byte[] scriptPubKey_A0 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress0.getBech32AsString(), params);
            _TransactionOutput output_A0 = new _TransactionOutput(params, null, Coin.valueOf(stonewall0.getSpendAmount()), scriptPubKey_A0);
            outputsA.put(output_A0, Triple.of(segwitAddress0.getECKey().getPubKey(), stonewall0.getFingerprintCollab(), "M/0/" + idx));

            // contributor change output
            idx = bip84Wallet.getWallet().getAccount(0).getChange().getAddrIdx();
            SegwitAddress segwitAddress1 = bip84Wallet.getAddressAt(0, 1, idx);
            byte[] scriptPubKey_A1 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress1.getBech32AsString(), params);
            _TransactionOutput output_A1 = new _TransactionOutput(params, null, Coin.valueOf(totalContributedAmount - stonewall0.getSpendAmount()), scriptPubKey_A1);
            outputsA.put(output_A1, Triple.of(segwitAddress1.getECKey().getPubKey(), stonewall0.getFingerprintCollab(), "M/1/" + idx));
            stonewall0.setCollabChange(segwitAddress1.getBech32AsString());
        }

        STONEWALLx2 stonewall1 = new STONEWALLx2(stonewall0);
        stonewall1.doStep1(inputsA, outputsA);

        return stonewall1;
    }

    //
    // sender
    //
    public STONEWALLx2 doSTONEWALLx2_2(STONEWALLx2 stonewall1, CahootsWallet cahootsWallet, long feePerB) throws Exception {

        Transaction transaction = stonewall1.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
            log.debug("step2 tx:" + transaction);
        }
        int nbIncomingInputs = transaction.getInputs().size();

        List<UTXO> utxos = cahootsWallet.getCahootsUTXO(stonewall1.getAccount());
        Collections.shuffle(utxos);

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<String> seenTxs = new ArrayList<String>();
        for (TransactionInput input : transaction.getInputs()) {
            if (!seenTxs.contains(input.getOutpoint().getHash().toString())) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
        }

        List<UTXO> selectedUTXO = new ArrayList<UTXO>();
        long totalSelectedAmount = 0L;
        int nbTotalSelectedOutPoints = 0;
        for (int step = 0; step < 3; step++) {

            if (stonewall1.getCounterpartyAccount() == 0) {
                step = 2;
            }

            List<String> _seenTxs = seenTxs;
            selectedUTXO = new ArrayList<UTXO>();
            nbTotalSelectedOutPoints = 0;
            for (UTXO utxo : utxos) {

                switch (step) {
                    case 0:
                        if (utxo.getPath() != null && utxo.getPath().length() > 3 && utxo.getPath().charAt(2) != '0') {
                            continue;
                        }
                        break;
                    case 1:
                        if (utxo.getPath() != null && utxo.getPath().length() > 3 && utxo.getPath().charAt(2) != '1') {
                            continue;
                        }
                        break;
                    default:
                        break;
                }

                UTXO _utxo = new UTXO();
                for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                    if (!_seenTxs.contains(outpoint.getTxHash().toString())) {
                        _utxo.getOutpoints().add(outpoint);
                        _seenTxs.add(outpoint.getTxHash().toString());
                    }
                }

                if (_utxo.getOutpoints().size() > 0) {
                    selectedUTXO.add(_utxo);
                    totalSelectedAmount += _utxo.getValue();
                    nbTotalSelectedOutPoints += _utxo.getOutpoints().size();
                    if (log.isDebugEnabled()) {
                        log.debug("BIP84 selected utxo:" + _utxo.getValue());
                    }
                }

                if (totalSelectedAmount > FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 4, 0, feePerB) + stonewall1.getSpendAmount() + SamouraiWalletConst.bDust.longValue()) {
                    break;
                }
            }
            if (totalSelectedAmount > FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 4, 0, feePerB) + stonewall1.getSpendAmount() + SamouraiWalletConst.bDust.longValue()) {
                break;
            }
        }
        if (!(totalSelectedAmount > FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 4, 0, feePerB) + stonewall1.getSpendAmount() + SamouraiWalletConst.bDust.longValue())) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        if (log.isDebugEnabled()) {
            log.debug("BIP84 selected utxos:" + selectedUTXO.size());
        }

        long fee = FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 4, 0, feePerB);
        if (log.isDebugEnabled()) {
            log.debug("fee:" + fee);
        }
        if (fee % 2L != 0) {
            fee++;
        }
        if (log.isDebugEnabled()) {
            log.debug("fee pair:" + fee);
        }
        stonewall1.setFeeAmount(fee);

        if (log.isDebugEnabled()) {
            log.debug("destination:" + stonewall1.getDestination());
        }
        if (transaction.getOutputs() != null && transaction.getOutputs().size() == 2) {

            int idx = -1;
            for (int i = 0; i < 2; i++) {
                byte[] buf = transaction.getOutputs().get(i).getScriptBytes();
                byte[] script = new byte[buf.length];
                script[0] = 0x00;
                System.arraycopy(buf, 1, script, 1, script.length - 1);
                if (log.isDebugEnabled()) {
                    log.debug("script:" + new Script(script).toString());
                    log.debug("script hex:" + Hex.toHexString(script));
                    log.debug("address from script:" + Bech32UtilGeneric.getInstance().getAddressFromScript(new Script(script), params));
                }
                if(Bech32UtilGeneric.getInstance().getAddressFromScript(new Script(script), params).equalsIgnoreCase(stonewall1.getCollabChange())) {
                    idx = i;
                    break;
                }
            }

            if(idx == 0 || idx == 1) {
                Coin value = transaction.getOutputs().get(idx).getValue();
                Coin _value = Coin.valueOf(value.longValue() - (fee / 2L));
                if (log.isDebugEnabled()) {
                    log.debug("output value post fee:" + _value);
                }
                transaction.getOutputs().get(idx).setValue(_value);
                stonewall1.getPSBT().setTransaction(transaction);
            }
            else {
                throw new Exception("Cannot compose #Cahoots: invalid tx outputs");
            }

        }
        else {
            log.error("outputs: "+transaction.getOutputs().size());
            log.error("tx:"+transaction.toString());
            throw new Exception("Cannot compose #Cahoots: invalid tx outputs count");
        }

        NetworkParameters params = stonewall1.getParams();

        //
        //
        // step2: B verif, utxos -> A (take smallest that cover amount)
        //
        //

        BIP84Wallet bip84Wallet = cahootsWallet.getBip84Wallet();
        String zpub = bip84Wallet.getWallet().getAccountAt(stonewall1.getAccount()).zpubstr();
        HashMap<_TransactionOutPoint, Triple<byte[], byte[], String>> inputsB = new HashMap<_TransactionOutPoint, Triple<byte[], byte[], String>>();

        for (UTXO utxo : selectedUTXO) {
            for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                _TransactionOutPoint _outpoint = new _TransactionOutPoint(outpoint);

                ECKey eckey = cahootsWallet.getPrivKey(_outpoint.getAddress(), stonewall1.getAccount());
                String path = cahootsWallet.getUnspentPath(_outpoint.getAddress());
                inputsB.put(_outpoint, Triple.of(eckey.getPubKey(), FormatsUtilGeneric.getInstance().getFingerprintFromXPUB(zpub), path));
            }
        }

        // spender change output
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsB = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        if (stonewall1.getAccount() == WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            int idx = cahootsWallet.getHighestPostChangeIdx();
            SegwitAddress segwitAddress = bip84Wallet.getAddressAt(stonewall1.getAccount(), 1, idx);byte[] scriptPubKey_B0 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress.getBech32AsString(), params);
            _TransactionOutput output_B0 = new _TransactionOutput(params, null, Coin.valueOf((totalSelectedAmount - stonewall1.getSpendAmount()) - (fee / 2L)), scriptPubKey_B0);
            outputsB.put(output_B0, Triple.of(segwitAddress.getECKey().getPubKey(), stonewall1.getFingerprint(), "M/1/" + idx));
        } else {
            int idx = bip84Wallet.getWallet().getAccount(0).getChange().getAddrIdx();
            SegwitAddress segwitAddress = bip84Wallet.getAddressAt(0, 1, idx);
            byte[] scriptPubKey_B0 = Bech32UtilGeneric.getInstance().computeScriptPubKey(segwitAddress.getBech32AsString(), params);
            _TransactionOutput output_B0 = new _TransactionOutput(params, null, Coin.valueOf((totalSelectedAmount - stonewall1.getSpendAmount()) - (fee / 2L)), scriptPubKey_B0);
            outputsB.put(output_B0, Triple.of(segwitAddress.getECKey().getPubKey(), stonewall1.getFingerprint(), "M/1/" + idx));
        }

        STONEWALLx2 stonewall2 = new STONEWALLx2(stonewall1);
        stonewall2.doStep2(inputsB, outputsB);

        return stonewall2;
    }

    //
    // counterparty
    //
    public STONEWALLx2 doSTONEWALLx2_3(STONEWALLx2 stonewall2, CahootsWallet cahootsWallet) throws Exception {
        int myAccount = stonewall2.getCounterpartyAccount();
        HashMap<String, ECKey> keyBag_A = computeKeyBag(stonewall2, myAccount, cahootsWallet);

        STONEWALLx2 stonewall3 = new STONEWALLx2(stonewall2);
        stonewall3.doStep3(keyBag_A);

        return stonewall3;
    }

    //
    // sender
    //
    public STONEWALLx2 doSTONEWALLx2_4(STONEWALLx2 stonewall3, CahootsWallet cahootsWallet) throws Exception {
        int myAccount = stonewall3.getAccount();
        HashMap<String, ECKey> keyBag_B = computeKeyBag(stonewall3, myAccount, cahootsWallet);

        STONEWALLx2 stonewall4 = new STONEWALLx2(stonewall3);
        stonewall4.doStep4(keyBag_B);

        return stonewall4;
    }

    private HashMap<String, ECKey> computeKeyBag(STONEWALLx2 stonewall, int myAccount, CahootsWallet cahootsWallet) {
        HashMap<String, String> utxo2Address = new HashMap<String, String>();
        List<UTXO> utxos = cahootsWallet.getCahootsUTXO(myAccount);
        for (UTXO utxo : utxos) {
            for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                utxo2Address.put(outpoint.getTxHash().toString() + "-" + outpoint.getTxOutputN(), outpoint.getAddress());
            }
        }

        Transaction transaction = stonewall.getTransaction();
        HashMap<String, ECKey> keyBag = new HashMap<String, ECKey>();
        for (TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();
            String key = outpoint.getHash().toString() + "-" + outpoint.getIndex();
            if (utxo2Address.containsKey(key)) {
                String address = utxo2Address.get(key);
                ECKey eckey = cahootsWallet.getPrivKey(address, myAccount);
                keyBag.put(outpoint.toString(), eckey);
            }
        }
        return keyBag;
    }
}
