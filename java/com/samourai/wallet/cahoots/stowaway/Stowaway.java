package com.samourai.wallet.cahoots.stowaway;

import com.samourai.wallet.bip69.BIP69InputComparator;
import com.samourai.wallet.bip69.BIP69OutputComparator;
import com.samourai.wallet.cahoots.Cahoots2x;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.psbt.PSBT;
import com.samourai.wallet.send.MyTransactionOutPoint;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Stowaway extends Cahoots2x {
    private static final Logger log = LoggerFactory.getLogger(Stowaway.class);

    private Stowaway()    { ; }

    public Stowaway(Stowaway stowaway)    {
        super(stowaway);
    }

    public Stowaway(JSONObject obj)    {
        this.fromJSON(obj);
    }

    public Stowaway(long spendAmount, NetworkParameters params, int account)    {
        this(spendAmount, params, null, null, account);
    }

    public Stowaway(long spendAmount, NetworkParameters params, String strPayNymInit, String strPayNymCollab, int account)    {
        super(CahootsType.STOWAWAY.getValue(), params, spendAmount, null, account);
        this.strPayNymInit = strPayNymInit;
        this.strPayNymCollab = strPayNymCollab;
    }

    //
    // receiver
    //
    public void doStep1(HashMap<MyTransactionOutPoint,Triple<byte[],byte[],String>> inputs, HashMap<TransactionOutput,Triple<byte[],byte[],String>> outputs) throws Exception    {

        if(this.getStep() != 0 || this.getSpendAmount() == 0L)   {
            throw new Exception("Invalid step/amount");
        }
        if(outputs == null)    {
            throw new Exception("Invalid outputs");
        }

        Transaction transaction = new Transaction(params);
        transaction.setVersion(2);
        for(MyTransactionOutPoint outpoint : inputs.keySet())   {
            TransactionInput input = outpoint.computeSpendInput();
            input.setSequenceNumber(SEQUENCE_RBF_ENABLED);
            if (log.isDebugEnabled()) {
                log.debug("input value:" + input.getValue().longValue());
            }
            transaction.addInput(input);
            outpoints.put(outpoint.getHash().toString() + "-" + outpoint.getIndex(), outpoint.getValue().longValue());
        }
        for(TransactionOutput output : outputs.keySet())   {
            transaction.addOutput(output);
        }

        // used by Sparrow
        String strBlockHeight = System.getProperty(BLOCK_HEIGHT_PROPERTY);
        if(strBlockHeight != null) {
            transaction.setLockTime(Long.parseLong(strBlockHeight));
        }

        this.psbt = new PSBT(transaction);

        if (log.isDebugEnabled()) {
            log.debug("input value:" + psbt.getTransaction().getInputs().get(0).getValue().longValue());
        }

        this.setStep(1);
    }

    //
    // sender
    //
    public void doStep2(HashMap<MyTransactionOutPoint,Triple<byte[],byte[],String>> inputs, HashMap<TransactionOutput,Triple<byte[],byte[],String>> outputs) throws Exception    {

        Transaction transaction = psbt.getTransaction();
        if (log.isDebugEnabled()) {
            log.debug("step2 tx:" + transaction.toString());
            log.debug("step2 tx:" + Hex.toHexString(transaction.bitcoinSerialize()));
        }

        // tx: modify spend output
        long contributedAmount = 0L;
        /*
        for(TransactionInput input : transaction.getInputs())   {
//            Log.d("Stowaway", input.getOutpoint().toString());
            contributedAmount += input.getOutpoint().getValue().longValue();
        }
        */
        for(String outpoint : outpoints.keySet())   {
            contributedAmount += outpoints.get(outpoint);
        }
        long outputAmount = transaction.getOutput(0).getValue().longValue();
        TransactionOutput spendOutput = transaction.getOutput(0);
        spendOutput.setValue(Coin.valueOf(outputAmount + contributedAmount));
        transaction.clearOutputs();
        transaction.addOutput(spendOutput);

        for(MyTransactionOutPoint outpoint : inputs.keySet())   {
            if (log.isDebugEnabled()) {
                log.debug("outpoint value:" + outpoint.getValue().longValue());
            }
            TransactionInput input = outpoint.computeSpendInput();
            input.setSequenceNumber(SEQUENCE_RBF_ENABLED);
            transaction.addInput(input);
            outpoints.put(outpoint.getHash().toString() + "-" + outpoint.getIndex(), outpoint.getValue().longValue());
        }
        for(TransactionOutput output : outputs.keySet())   {
            transaction.addOutput(output);
        }

        psbt = new PSBT(transaction);

        this.setStep(2);
    }

    //
    // receiver
    //
    public void doStep3(HashMap<String,ECKey> keyBag)    {

        Transaction transaction = this.getTransaction();
        List<TransactionInput> inputs = new ArrayList<TransactionInput>();
        inputs.addAll(transaction.getInputs());
        Collections.sort(inputs, new BIP69InputComparator());
        transaction.clearInputs();
        for(TransactionInput input : inputs)    {
            transaction.addInput(input);
        }
        List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
        outputs.addAll(transaction.getOutputs());
        Collections.sort(outputs, new BIP69OutputComparator());
        transaction.clearOutputs();
        for(TransactionOutput output : outputs)    {
            transaction.addOutput(output);
        }

        psbt = new PSBT(transaction);

        signTx(keyBag);

        this.setStep(3);
    }

    //
    // sender
    //
    public void doStep4(HashMap<String,ECKey> keyBag)    {

        signTx(keyBag);

        this.setStep(4);
    }
}
