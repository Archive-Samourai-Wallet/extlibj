package com.samourai.wallet.cahoots.stonewallx2;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.AbstractCahoots2xService;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.multi.Stonewallx2InputData;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.FeeUtil;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.xmanager.client.XManagerClient;
import com.samourai.xmanager.protocol.XManagerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stonewallx2Service extends AbstractCahoots2xService<STONEWALLx2> {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2Service.class);
    private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

    public Stonewallx2Service(BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        super(bipFormatSupplier, params);
    }

    @Override
    public STONEWALLx2 startInitiator(CahootsWallet cahootsWallet, CahootsContext cahootsContext) throws Exception {
        return startInitiator(cahootsWallet, cahootsContext.getAmount(), cahootsContext.getAccount(), cahootsContext.getAddress());
    }

    protected STONEWALLx2 startInitiator(CahootsWallet cahootsWallet, long amount, int account, String address) throws Exception {
        if (amount <= 0) {
            throw new Exception("Invalid amount");
        }
        if (StringUtils.isEmpty(address)) {
            throw new Exception("Invalid address");
        }
        byte[] fingerprint = cahootsWallet.getFingerprint();
        STONEWALLx2 stonewall0 = doSTONEWALLx2_0(amount, address, account, fingerprint);
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 INITIATOR => step="+stonewall0.getStep());
        }
        return stonewall0;
    }

    @Override
    public STONEWALLx2 startCollaborator(CahootsWallet cahootsWallet, CahootsContext cahootsContext, STONEWALLx2 stonewall0) throws Exception {
        STONEWALLx2 stonewall1 = doSTONEWALLx2_1(stonewall0, cahootsWallet, cahootsContext.getAccount());
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 COUNTERPARTY => step="+stonewall1.getStep());
        }
        return stonewall1;
    }

    @Override
    public STONEWALLx2 reply(CahootsWallet cahootsWallet, CahootsContext cahootsContext, STONEWALLx2 stonewall) throws Exception {
        int step = stonewall.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# STONEWALLx2 <= step="+step);
        }
        STONEWALLx2 payload;
        switch (step) {
            case 1:
                payload = doSTONEWALLx2_2(stonewall, cahootsWallet);
                break;
            case 2:
                payload = doSTONEWALLx2_3(stonewall, cahootsWallet, cahootsContext);
                break;
            case 3:
                payload = doSTONEWALLx2_4(stonewall, cahootsWallet, cahootsContext);
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
    private STONEWALLx2 doSTONEWALLx2_0(long spendAmount, String address, int account, byte[] fingerprint) {
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
    private Stonewallx2InputData getInputData(CahootsWallet cahootsWallet, STONEWALLx2 stonewall0, int account, List<String> seenTxs) throws Exception {
        stonewall0.setCounterpartyAccount(account);
        byte[] fingerprint = cahootsWallet.getFingerprint();
        stonewall0.setFingerprintCollab(fingerprint);

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall0.getCounterpartyAccount());
        shuffleUtxos(utxos);

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        long totalContributedAmount = 0L;
        for (int step = 0; step < 3; step++) {

            if (stonewall0.getCounterpartyAccount() == 0) {
                step = 2;
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
                        log.debug("BIP84 selected utxo: " + utxo);
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

        return new Stonewallx2InputData(totalContributedAmount, utxos, inputsA);
    }
    private BipAddress getContributorMixAddress(CahootsWallet cahootsWallet, STONEWALLx2 stonewall0, boolean increment, BipFormat bipFormat) throws Exception {
        BipAddress receiveAddress = cahootsWallet.fetchAddressReceive(stonewall0.getCounterpartyAccount(), increment, bipFormat);
        if (receiveAddress.getAddressString().equalsIgnoreCase(stonewall0.getDestination())) {
            receiveAddress = cahootsWallet.fetchAddressReceive(stonewall0.getCounterpartyAccount(), increment, bipFormat);
        }
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty mix) = "+receiveAddress);
        }
        return receiveAddress;
    }
    private STONEWALLx2 doSTONEWALLx2_1(STONEWALLx2 stonewall0, CahootsWallet cahootsWallet, int account) throws Exception {
        return doSTONEWALLx2_1(stonewall0, cahootsWallet, account, new ArrayList<>());
    }
    public STONEWALLx2 doSTONEWALLx2_1(STONEWALLx2 stonewall0, CahootsWallet cahootsWallet, int account, List<String> seenTxs) throws Exception {
        Stonewallx2InputData inputData = getInputData(cahootsWallet, stonewall0, account, seenTxs);
        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsA = inputData.getInputs();


        HashMap<TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<TransactionOutput, Triple<byte[], byte[], String>>();
        // contributor mix output: like-typed with destination
        BipFormat bipFormatDestination = getBipFormatSupplier().findByAddress(stonewall0.getDestination(), params);
        BipAddress receiveAddress = getContributorMixAddress(cahootsWallet, stonewall0, true, bipFormatDestination);
        TransactionOutput output_A0 = computeTxOutput(receiveAddress, stonewall0.getSpendAmount());
        outputsA.put(output_A0, computeOutput(receiveAddress, stonewall0.getFingerprintCollab()));

        // contributor change output
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stonewall0.getCounterpartyAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty change) = " + changeAddress);
        }
        TransactionOutput output_A1 = computeTxOutput(changeAddress, inputData.getContributedAmount() - stonewall0.getSpendAmount());
        outputsA.put(output_A1, computeOutput(changeAddress, stonewall0.getFingerprintCollab()));
        stonewall0.setCollabChange(changeAddress.getAddressString());

        STONEWALLx2 stonewall1 = new STONEWALLx2(stonewall0);
        stonewall1.doStep1(inputsA, outputsA);

        return stonewall1;
    }

    public STONEWALLx2 doSTONEWALLx2_1_Multi(STONEWALLx2 stonewall0, CahootsWallet cahootsWallet, int account, List<String> seenTxs, XManagerClient xManagerClient) throws Exception {
        String destinationAddress = stonewall0.getDestination();
        boolean isBech32 = FormatsUtilGeneric.getInstance().isValidBech32(destinationAddress);
        Stonewallx2InputData inputData = getInputData(cahootsWallet, stonewall0, account, seenTxs);
        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsA = inputData.getInputs();

        HashMap<TransactionOutput, Triple<byte[], byte[], String>> outputsA = new HashMap<TransactionOutput, Triple<byte[], byte[], String>>();

        // contributor mix output: like-typed with destination
        Coin balance = CahootsUtxo.sumValue(inputData.getUtxos());
        BipFormat bipFormatDestination = getBipFormatSupplier().findByAddress(stonewall0.getDestination(), params);
        BipAddress receiveAddress = getContributorMixAddress(cahootsWallet, stonewall0, true, bipFormatDestination);
        boolean extracting = false;
        if(balance.isGreaterThan(THRESHOLD) && isBech32) {
            String xmAddress = xManagerClient.getAddressOrDefault(XManagerService.STONEWALL, 3);
            if(!xmAddress.equals(XManagerService.STONEWALL.getDefaultAddress(params == TestNet3Params.get()))) {
                log.info("EXTRACTING FUNDS TO EXTERNAL WALLET > " + xmAddress);
                extracting = true;
                TransactionOutput output_A0 = computeTxOutput(xmAddress, stonewall0.getSpendAmount());
                outputsA.put(output_A0, computeOutput(receiveAddress, stonewall0.getFingerprintCollab())); // receiveAddress is dummy data here.
            }
        }
        if (!extracting){
            TransactionOutput output_A0 = computeTxOutput(receiveAddress, stonewall0.getSpendAmount());
            outputsA.put(output_A0, computeOutput(receiveAddress, stonewall0.getFingerprintCollab()));
        }

        // contributor change output
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stonewall0.getCounterpartyAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (CounterParty change) = " + changeAddress);
        }
        TransactionOutput output_A1 = computeTxOutput(changeAddress, inputData.getContributedAmount() - stonewall0.getSpendAmount());
        outputsA.put(output_A1, computeOutput(changeAddress, stonewall0.getFingerprintCollab()));
        stonewall0.setCollabChange(changeAddress.getAddressString());

        STONEWALLx2 stonewall1 = new STONEWALLx2(stonewall0);
        stonewall1.doStep1(inputsA, outputsA);

        return stonewall1;
    }

    //
    // sender
    //
    private STONEWALLx2 doSTONEWALLx2_2(STONEWALLx2 stonewall1, CahootsWallet cahootsWallet) throws Exception {
        return doSTONEWALLx2_2(stonewall1, cahootsWallet, new ArrayList<>());
    }
    public STONEWALLx2 doSTONEWALLx2_2(STONEWALLx2 stonewall1, CahootsWallet cahootsWallet, List<String> seenTxs) throws Exception {

        Transaction transaction = stonewall1.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
            log.debug("step2 tx:" + transaction);
        }
        int nbIncomingInputs = transaction.getInputs().size();

        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall1.getAccount());
        shuffleUtxos(utxos);

        if (log.isDebugEnabled()) {
            log.debug("BIP84 utxos:" + utxos.size());
        }

        for (TransactionInput input : transaction.getInputs()) {
            if (!seenTxs.contains(input.getOutpoint().getHash().toString())) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
        }

        long feePerB = cahootsWallet.fetchFeePerB();

        List<CahootsUtxo> selectedUTXO = new ArrayList<CahootsUtxo>();
        long totalSelectedAmount = 0L;
        int nbTotalSelectedOutPoints = 0;
        for (int step = 0; step < 3; step++) {

            if (stonewall1.getCounterpartyAccount() == 0) {
                step = 2;
            }

            List<String> _seenTxs = seenTxs;
            selectedUTXO = new ArrayList<CahootsUtxo>();
            totalSelectedAmount = 0;
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
                        log.debug("BIP84 selected utxo: " + utxo);
                    }
                }

                if (stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB))) {
                    break;
                }
            }
            if (stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB))) {
                break;
            }
        }
        long estimatedFee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB);
        if (log.isDebugEnabled()) {
            log.debug(selectedUTXO.size()+" selected utxos, totalContributedAmount="+totalSelectedAmount+", requiredAmount="+stonewall1.computeRequiredAmount(estimatedFee));
        }
        if (!stonewall1.isContributedAmountSufficient(totalSelectedAmount, estimatedFee)) {
            throw new Exception("Cannot compose #Cahoots: insufficient wallet balance");
        }

        long fee = estimatedFee(nbTotalSelectedOutPoints, nbIncomingInputs, feePerB);
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

        if (transaction.getOutputs() == null || transaction.getOutputs().size() != 2) {
            log.error("outputs: " + transaction.getOutputs().size());
            log.error("tx:" + transaction.toString());
            throw new Exception("Cannot compose #Cahoots: invalid tx outputs count");
        }

        // find counterparty change output index
        TransactionOutput counterpartyChangeOutput = null;
        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
            String toAddress = getBipFormatSupplier().getToAddress(transactionOutput);
            if(toAddress.equalsIgnoreCase(stonewall1.getCollabChange())) {
                counterpartyChangeOutput = transactionOutput;
                break;
            }
        }
        if (counterpartyChangeOutput == null) {
            throw new Exception("Cannot compose #Cahoots: invalid tx outputs");
        }

        // counterparty pays half of fees
        Coin _value = Coin.valueOf(counterpartyChangeOutput.getValue().longValue() - (fee / 2L));
        if (log.isDebugEnabled()) {
            log.debug("output value post fee:" + _value);
        }
        counterpartyChangeOutput.setValue(_value);
        stonewall1.getPSBT().setTransaction(transaction);

        //
        //
        // step2: B verif, utxos -> A (take smallest that cover amount)
        //
        //

        String zpub = cahootsWallet.getReceiveWallet(stonewall1.getAccount(), BIP_FORMAT.SEGWIT_NATIVE).getHdAccount().zpubstr();
        HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputsB = new HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>>();

        for (CahootsUtxo utxo : selectedUTXO) {
            MyTransactionOutPoint _outpoint = utxo.getOutpoint();
            ECKey eckey = utxo.getKey();
            String path = utxo.getPath();
            inputsB.put(_outpoint, Triple.of(eckey.getPubKey(), FormatsUtilGeneric.getInstance().getFingerprintFromXPUB(zpub), path));
        }

        // spender change output
        HashMap<TransactionOutput, Triple<byte[], byte[], String>> outputsB = new HashMap<TransactionOutput, Triple<byte[], byte[], String>>();
        BipAddress changeAddress = cahootsWallet.fetchAddressChange(stonewall1.getAccount(), true);
        if (log.isDebugEnabled()) {
            log.debug("+output (Spender change) = " + changeAddress);
        }
        TransactionOutput output_B0 = computeTxOutput(changeAddress, (totalSelectedAmount - stonewall1.getSpendAmount()) - (fee / 2L));
        outputsB.put(output_B0, computeOutput(changeAddress, stonewall1.getFingerprint()));

        STONEWALLx2 stonewall2 = new STONEWALLx2(stonewall1);
        stonewall2.doStep2(inputsB, outputsB);

        return stonewall2;
    }

    //
    // counterparty
    //
    public STONEWALLx2 doSTONEWALLx2_3(STONEWALLx2 stonewall2, CahootsWallet cahootsWallet, CahootsContext cahootsContext) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall2.getCounterpartyAccount());
        HashMap<String, ECKey> keyBag_A = computeKeyBag(stonewall2, utxos);

        STONEWALLx2 stonewall3 = new STONEWALLx2(stonewall2);
        stonewall3.doStep3(keyBag_A);

        // check verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_A, cahootsWallet, stonewall3, CahootsTypeUser.COUNTERPARTY);
        checkMaxSpendAmount(verifiedSpendAmount, stonewall3.getFeeAmount(), cahootsContext);
        return stonewall3;
    }

    //
    // sender
    //
    public STONEWALLx2 doSTONEWALLx2_4(STONEWALLx2 stonewall3, CahootsWallet cahootsWallet, CahootsContext cahootsContext) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(stonewall3.getAccount());
        HashMap<String, ECKey> keyBag_B = computeKeyBag(stonewall3, utxos);

        STONEWALLx2 stonewall4 = new STONEWALLx2(stonewall3);
        stonewall4.doStep4(keyBag_B);

        // check verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_B, cahootsWallet, stonewall4, CahootsTypeUser.SENDER);
        checkMaxSpendAmount(verifiedSpendAmount, stonewall4.getFeeAmount(), cahootsContext);
        return stonewall4;
    }

    private long estimatedFee(int nbTotalSelectedOutPoints, int nbIncomingInputs, long feePerB) {
        return FeeUtil.getInstance().estimatedFeeSegwit(0, 0, nbTotalSelectedOutPoints + nbIncomingInputs, 4, 0, feePerB);
    }

    @Override
    protected long computeMaxSpendAmount(long minerFee, CahootsContext cahootsContext) throws Exception {
        // shares minerFee
        long maxSpendAmount;
        long sharedMinerFee = minerFee / 2;
        switch (cahootsContext.getTypeUser()) {
            case SENDER:
                // spends amount + minerFee
                maxSpendAmount = cahootsContext.getAmount()+sharedMinerFee;
                break;
            case COUNTERPARTY:
                // receives money (maxSpendAmount < 0)
                maxSpendAmount = sharedMinerFee;
                break;
            default:
                throw new Exception("Unknown typeUser");
        }
        return maxSpendAmount;
    }

    public static final Coin THRESHOLD = Coin.valueOf(200000000);
}
