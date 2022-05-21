package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.*;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.FeeUtil;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultiCahootsService extends AbstractCahootsService<MultiCahoots> {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsService.class);
    private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

    public MultiCahootsService(BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        super(bipFormatSupplier, params);
    }

    public MultiCahoots startInitiator(CahootsWallet cahootsWallet, String address, long amount, int account) throws Exception {
        if (amount <= 0) {
            throw new Exception("Invalid amount");
        }
        byte[] fingerprint = cahootsWallet.getBip84Wallet().getFingerprint();
        MultiCahoots stowaway0 = doMultiCahoots0_Stowaway0(address, amount, account, fingerprint);
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway INITIATOR => step="+stowaway0.getStep());
        }
        return stowaway0;
    }

    @Override
    public MultiCahoots startCollaborator(CahootsWallet cahootsWallet, int account, MultiCahoots stowaway0) throws Exception {
        MultiCahoots stowaway1 = doMultiCahoots1_Stowaway1(stowaway0, cahootsWallet, account);
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway COUNTERPARTY => step="+stowaway1.getStep());
        }
        return stowaway1;
    }

    @Override
    public MultiCahoots reply(CahootsWallet cahootsWallet, MultiCahoots stowaway) throws Exception {
        int step = stowaway.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway <= step="+step);
        }
        MultiCahoots payload;
        switch (step) {
            case 1:
                payload = doMultiCahoots2_Stowaway2(stowaway, cahootsWallet);
                break;
            case 2:
                payload = doMultiCahoots3_Stowaway3(stowaway, cahootsWallet);
                break;
            case 3:
                payload = doMultiCahoots4_Stowaway4(stowaway, cahootsWallet);
                break;
            case 4:
                payload = doMultiCahoots5_Stonewallx20_StartInitiator(stowaway, cahootsWallet);
                break;
            case 5:
                payload = doMultiCahoots6_Stonewallx21_StartCollaborator(stowaway, cahootsWallet, stowaway.getAccount());
                break;
            case 6:
                payload = doMultiCahoots7_Stonewallx22(stowaway, cahootsWallet);
                break;
            case 7:
                payload = doMultiCahoots8_Stonewallx23(stowaway, cahootsWallet);
                break;
            case 8:
                payload = doMultiCahoots9_Stonewallx24(stowaway, cahootsWallet);
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
    private MultiCahoots doMultiCahoots0_Stowaway0(String address, long spendAmount, int account, byte[] fingerprint) {
        //
        //
        // step0: B sends spend amount to A,  creates step0
        //
        //
        if (log.isDebugEnabled()) {
            log.debug("sender account (0):" + account);
        }
        MultiCahoots stowaway0 = new MultiCahoots(address, spendAmount, params, account);
        stowaway0 = stowaway0.doStep0_Stowaway_StartInitiator(address, spendAmount, account, fingerprint);
        return stowaway0;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots1_Stowaway1(MultiCahoots stowaway0, CahootsWallet cahootsWallet, int account) throws Exception {
        byte[] fingerprint = cahootsWallet.getBip84Wallet().getFingerprint();
        stowaway0.setFingerprintCollab(fingerprint);

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(account);
        // No need to filter out UTXOs that have been used in a previous part of the Cahoots, as this is the first time we select UTXOs.
        // sort in descending order by value
        utxos.sort(new UTXO.UTXOComparator());
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
            SecureRandom random = RandomUtil.getSecureRandom();
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
        BipAddress receiveAddress = cahootsWallet.fetchAddressReceive(account, true);
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty receive) = "+receiveAddress);
        }
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        _TransactionOutput output_A0 = computeTxOutput(receiveAddress, stowaway0.getSpendAmount());
        outputsA.put(output_A0, computeOutput(receiveAddress, stowaway0.getFingerprintCollab()));

        stowaway0.setDestination(receiveAddress.getAddressString());
        stowaway0.setCounterpartyAccount(account);

        MultiCahoots stowaway1 = new MultiCahoots(stowaway0);
        stowaway1.doStep1_Stowaway_StartCollaborator(inputsA, outputsA);

        return stowaway1;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots2_Stowaway2(MultiCahoots stowaway1, CahootsWallet cahootsWallet) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("sender account (2):" + stowaway1.getAccount());
        }

        Transaction transaction = stowaway1.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
        }
        int nbIncomingInputs = transaction.getInputs().size();

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stowaway1.getAccount());
        // This is the first time we fetch UTXOs on initiator side. No need to filter yet.
        // sort in ascending order by value
        utxos.sort(new UTXO.UTXOComparator());
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

        long feePerB = cahootsWallet.fetchFeePerB();

        List<List<CahootsUtxo>> listOfLists = new ArrayList<List<CahootsUtxo>>();
        Collections.shuffle(lowUTXO);
        listOfLists.add(lowUTXO);
        listOfLists.add(utxos);
        int OUTPUTS_STOWAWAY = 2;
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
                if (stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STOWAWAY))) {

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
                        if (stowaway1.isContributedAmountSufficient(_totalSelectedAmount, estimatedFee(_nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STOWAWAY))) {
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
            if (stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STOWAWAY))) {
                break;
            }
        }

        long estimatedFee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STOWAWAY);
        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalSelectedAmount+", requiredAmount="+stowaway1.computeRequiredAmount(estimatedFee));
        }
        if (!stowaway1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        long fee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STOWAWAY);
        if (log.isDebugEnabled()) {
            log.debug("fee:" + fee);
        }

        NetworkParameters params = stowaway1.getParams();

        //
        //
        // step2: B verif, utxos -> A (take smallest that cover amount)
        //
        //

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
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stowaway1.getAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (sender change) = "+changeAddress);
        }
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsB = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        _TransactionOutput output_B0 = computeTxOutput(changeAddress, (totalSelectedAmount - stowaway1.getSpendAmount()) - fee);
        outputsB.put(output_B0, computeOutput(changeAddress, stowaway1.getFingerprint()));

        if (log.isDebugEnabled()) {
            log.debug("outputsB:" + outputsB.size());
        }

        MultiCahoots stowaway2 = new MultiCahoots(stowaway1);
        stowaway2.doStep2_Stowaway(inputsB, outputsB);
        stowaway2.setFeeAmount(fee);

        return stowaway2;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots3_Stowaway3(MultiCahoots stowaway2, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stowaway2.getCounterpartyAccount());
        HashMap<String, ECKey> keyBag_A = computeKeyBag(stowaway2, utxos);

        MultiCahoots stowaway3 = new MultiCahoots(stowaway2);
        stowaway3.doStep3_Stowaway(keyBag_A);

        // compute verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_A, cahootsWallet, stowaway3, CahootsTypeUser.COUNTERPARTY);
        stowaway3.setVerifiedSpendAmount(verifiedSpendAmount);
        return stowaway3;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots4_Stowaway4(MultiCahoots stowaway3, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stowaway3.getAccount());
        HashMap<String, ECKey> keyBag_B = computeKeyBag(stowaway3, utxos);

        MultiCahoots stowaway4 = new MultiCahoots(stowaway3);
        stowaway4.doStep4_Stowaway(keyBag_B);

        // compute verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_B, cahootsWallet, stowaway4, CahootsTypeUser.SENDER);
        stowaway4.setVerifiedSpendAmount(verifiedSpendAmount);
        return stowaway4;
    }

    //
    // counterparty
    //
    public MultiCahoots doMultiCahoots5_Stonewallx20_StartInitiator(MultiCahoots multiCahoots, CahootsWallet cahootsWallet) throws Exception {
        if (multiCahoots.getStonewallAmount() <= 0) {
            throw new Exception("Invalid amount");
        }
        if (StringUtils.isEmpty(multiCahoots.getStonewallDestination())) {
            throw new Exception("Invalid address");
        }

        MultiCahoots stonewall0 = new MultiCahoots(multiCahoots.getStonewallDestination(), multiCahoots.getStonewallAmount(), multiCahoots.getParams(), multiCahoots.getAccount());
        stonewall0.setStep(5);
        // Testing this out, might need to "fake" the initiation so the fingerprints don't change from prior step.
        stonewall0.setFingerprint(multiCahoots.getFingerprint());
        stonewall0.setFingerprintCollab(multiCahoots.getFingerprintCollab());
        stonewall0.setCounterpartyAccount(multiCahoots.getCounterpartyAccount());
        stonewall0.setDestination(multiCahoots.getStonewallDestination());

        stonewall0.setStowawayTransaction(multiCahoots.getStowawayTransaction());
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 INITIATOR => step="+stonewall0.getStep());
        }
        return stonewall0;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots6_Stonewallx21_StartCollaborator(MultiCahoots stonewall0, CahootsWallet cahootsWallet, int account) throws Exception {

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall0.getAccount());

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        long totalContributedAmount = 0L;
        for (int step = 0; step < 3; step++) {

            if (stonewall0.getCounterpartyAccount() == 0) {
                step = 2;
            }

            List<String> seenTxs = new ArrayList<String>();
            for(TransactionInput input : stonewall0.getStowawayTransaction().getInputs()) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
            selectedUTXO = new ArrayList<CahootsUtxo>();
            totalContributedAmount = 0L;
            for (CahootsUtxo utxo : utxos) {

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

                MyTransactionOutPoint outpoint = utxo.getOutpoint();
                if (!seenTxs.contains(outpoint.getHash().toString())) {
                    seenTxs.add(outpoint.getHash().toString());

                    selectedUTXO.add(utxo);
                    totalContributedAmount += utxo.getValue();
                    if (log.isDebugEnabled()) {
                        log.debug("BIP84 selected utxo:" + utxo.getValue());
                    }
                }

                if (stonewall0.isContributedAmountSufficient(totalContributedAmount)) {
                    break;
                }
            }
            if (stonewall0.isContributedAmountSufficient(totalContributedAmount)) {
                break;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalContributedAmount+", requiredAmount="+stonewall0.computeRequiredAmount());
        }
        if (!stonewall0.isContributedAmountSufficient(totalContributedAmount)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        NetworkParameters params = stonewall0.getParams();

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
            inputsA.put(_outpoint, Triple.of(eckey.getPubKey(), stonewall0.getFingerprintCollab(), path));
        }

        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        // contributor mix output
        BipAddress receiveAddress = cahootsWallet.fetchAddressReceive(stonewall0.getCounterpartyAccount(), true);
        if (receiveAddress.getAddressString().equalsIgnoreCase(stonewall0.getDestination())) {
            receiveAddress = cahootsWallet.fetchAddressReceive(stonewall0.getCounterpartyAccount(), true);
        }
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty mix) = "+receiveAddress);
        }
        _TransactionOutput output_A0 = computeTxOutput(receiveAddress, stonewall0.getSpendAmount());
        outputsA.put(output_A0, computeOutput(receiveAddress, stonewall0.getFingerprintCollab()));

        // contributor change output
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stonewall0.getAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty change) = " + changeAddress);
        }
        _TransactionOutput output_A1 = computeTxOutput(changeAddress, totalContributedAmount - stonewall0.getSpendAmount());
        outputsA.put(output_A1, computeOutput(changeAddress, stonewall0.getFingerprintCollab()));

        MultiCahoots stonewall1 = new MultiCahoots(stonewall0);
        stonewall1.doStep6_Stonewallx2_StartCollaborator(inputsA, outputsA);

        return stonewall1;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots7_Stonewallx22(MultiCahoots stonewall1, CahootsWallet cahootsWallet) throws Exception {

        Transaction transaction = stonewall1.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
            log.debug("step2 tx:" + transaction);
        }
        int nbIncomingInputs = transaction.getInputs().size();

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall1.getCounterpartyAccount());

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<String> seenTxs = new ArrayList<String>();
        for (TransactionInput input : transaction.getInputs()) {
            if (!seenTxs.contains(input.getOutpoint().getHash().toString())) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
        }

        for(TransactionInput input : stonewall1.getStowawayTransaction().getInputs()) {
            seenTxs.add(input.getOutpoint().getHash().toString());
        }

        long feePerB = cahootsWallet.fetchFeePerB();

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        long totalSelectedAmount = 0L;
        int nbTotalSelectedOutPoints = 0;
        int OUTPUTS_STONEWALL = 4;
        for (int step = 0; step < 3; step++) {

            if (stonewall1.getCounterpartyAccount() == 0) {
                step = 2;
            }

            List<String> _seenTxs = seenTxs;
            selectedUTXO = new ArrayList<CahootsUtxo>();
            nbTotalSelectedOutPoints = 0;
            for (CahootsUtxo utxo : utxos) {

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

                if (!_seenTxs.contains(utxo.getOutpoint().getHash().toString())) {
                    _seenTxs.add(utxo.getOutpoint().getHash().toString());

                    selectedUTXO.add(utxo);
                    totalSelectedAmount += utxo.getValue();
                    nbTotalSelectedOutPoints ++;
                    if (log.isDebugEnabled()) {
                        log.debug("BIP84 selected utxo:" + utxo.getValue());
                    }
                }

                if (stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STONEWALL))) {
                    break;
                }
            }
            if (stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STONEWALL))) {
                break;
            }
        }
        long estimatedFee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STONEWALL);
        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalSelectedAmount+", requiredAmount="+stonewall1.computeRequiredAmount(estimatedFee));
        }
        if (!stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        long fee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB, OUTPUTS_STONEWALL);
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
            for (int i = 0; i < transaction.getOutputs().size(); i++) {
                TransactionOutput utxo = transaction.getOutput(i);
                if(utxo.getValue().value != stonewall1.getSpendAmount() && !bech32Util.getAddressFromScript(utxo.getScriptPubKey(), params).equalsIgnoreCase(stonewall1.getCollabChange())) {
                    // find user's change output, it is the output that does not equal our change address, and does not equal the stonewall amount
                    idx = i;
                    break;
                }
            }

            if(idx != -1) {
                Coin value = transaction.getOutputs().get(idx).getValue();
                Coin _value = Coin.valueOf(value.longValue() - (fee));
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

        String zpub = cahootsWallet.getBip84Wallet().getAccount(stonewall1.getAccount()).zpubstr();
        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsB = new HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>>();

        for (CahootsUtxo utxo : selectedUTXO) {
            MyTransactionOutPoint _outpoint = utxo.getOutpoint();
            ECKey eckey = utxo.getKey();
            String path = utxo.getPath();
            inputsB.put(_outpoint, Triple.of(eckey.getPubKey(), FormatsUtilGeneric.getInstance().getFingerprintFromXPUB(zpub), path));
        }

        // spender change output
        HashMap<_TransactionOutput, Triple<byte[], byte[], String>> outputsB = new HashMap<_TransactionOutput, Triple<byte[], byte[], String>>();
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stonewall1.getCounterpartyAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (Spender change) = " + changeAddress);
        }
        _TransactionOutput output_B0 = computeTxOutput(changeAddress, (totalSelectedAmount - stonewall1.getSpendAmount()));
        outputsB.put(output_B0, computeOutput(changeAddress, stonewall1.getFingerprint()));
        stonewall1.setCollabChange(changeAddress.getAddressString());

        MultiCahoots stonewall2 = new MultiCahoots(stonewall1);
        stonewall2.doStep7_Stonewallx2(inputsB, outputsB);

        return stonewall2;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots8_Stonewallx23(MultiCahoots stonewall2, CahootsWallet cahootsWallet) throws Exception {
        System.out.println("PERFORMING doMultiCahoots8_Stonewallx23");
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall2.getAccount());
        HashMap<String, ECKey> keyBag_A = computeKeyBag(stonewall2, utxos);

        MultiCahoots stonewall3 = new MultiCahoots(stonewall2);
        stonewall3.doStep8_Stonewallx2(keyBag_A);

        // compute verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_A, cahootsWallet, stonewall3, CahootsTypeUser.COUNTERPARTY);
        stonewall3.setVerifiedSpendAmount(verifiedSpendAmount);
        return stonewall3;
    }

    private boolean checkForNoFee(MultiCahoots multiCahoots, List<CahootsUtxo> utxos) {
        System.out.println(multiCahoots.getTransaction().toString());
        long inputSum = 0;
        long outputSum = 0;

        for(int i = 0; i < multiCahoots.getTransaction().getInputs().size(); i++) {
            TransactionInput input = multiCahoots.getTransaction().getInput(i);
            for(CahootsUtxo cahootsUtxo : utxos) {
                int outpointIndex = cahootsUtxo.getOutpoint().getTxOutputN();
                Sha256Hash outpointHash = cahootsUtxo.getOutpoint().getTxHash();
                if(input != null && input.getOutpoint().getHash().equals(outpointHash) && input.getOutpoint().getIndex() == outpointIndex) {
                    long amount = cahootsUtxo.getValue();
                    inputSum += amount;
                }
            }
        }
        for(int i = 0; i < multiCahoots.getTransaction().getOutputs().size(); i++) {
            TransactionOutput utxo = multiCahoots.getTransaction().getOutput(i);
            long amount = utxo.getValue().value;
            String address = null;
            try {
                address = bech32Util.getAddressFromScript(utxo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(address != null && address.equals(multiCahoots.getCollabChange())) {
                System.out.println("Adding change " + amount);
                outputSum += amount;
            } else if(address != null && amount == multiCahoots.getSpendAmount() && !address.equals(multiCahoots.getDestination())) {
                System.out.println("Adding " + amount);
                outputSum += amount;
            }
        }

        System.out.println("INPUT" + inputSum);
        System.out.println("OUTPUT " + outputSum);
        return (inputSum - outputSum) == 0 && inputSum != 0 && outputSum != 0;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots9_Stonewallx24(MultiCahoots stonewall3, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall3.getCounterpartyAccount());
        HashMap<String, ECKey> keyBag_B = computeKeyBag(stonewall3, utxos);

        MultiCahoots stonewall4 = new MultiCahoots(stonewall3);
        boolean noFeeTaken = checkForNoFee(stonewall4, utxos);
        if(noFeeTaken) {
            stonewall4.doStep9_Stonewallx2(keyBag_B);
        } else {
            throw new Exception("Cannot compose #Cahoots: fee is being taken from us");
        }

        // compute verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_B, cahootsWallet, stonewall4, CahootsTypeUser.SENDER);
        stonewall4.setVerifiedSpendAmount(verifiedSpendAmount);
        return stonewall4;
    }

    private long estimatedFee(int nbTotalSelectedOutPoints, int nbIncomingInputs, long feePerB, int outputsNonOpReturn) {
        return FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, outputsNonOpReturn, 0, feePerB);
    }
}
