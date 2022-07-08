package com.samourai.wallet.cahoots.stonewallx2;

import com.samourai.wallet.bip69.BIP69InputComparator;
import com.samourai.wallet.bip69.BIP69OutputComparator;
import com.samourai.wallet.cahoots.Cahoots2x;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.psbt.PSBT;
import com.samourai.wallet.segwit.bech32.Bech32Segwit;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class STONEWALLx2 extends Cahoots2x {
    private static final Logger log = LoggerFactory.getLogger(STONEWALLx2.class);

    private STONEWALLx2()    { ; }

    public STONEWALLx2(STONEWALLx2 stonewall)    {
        super(stonewall);
    }

    public STONEWALLx2(JSONObject obj)    {
        this.fromJSON(obj);
    }

    public STONEWALLx2(long spendAmount, String address, NetworkParameters params, int account)    {
        super(CahootsType.STONEWALLX2.getValue(), params, spendAmount, address, account);
    }

    //
    // counterparty
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

        TransactionOutput _output = null;
        if(!FormatsUtilGeneric.getInstance().isValidBitcoinAddress(strDestination, params)) {
            throw new Exception("Invalid destination address");
        }
        if(FormatsUtilGeneric.getInstance().isValidBech32(strDestination))    {
            Pair<Byte, byte[]> pair = Bech32Segwit.decode(params instanceof TestNet3Params ? "tb" : "bc", strDestination);
            byte[] scriptPubKey = Bech32Segwit.getScriptPubkey(pair.getLeft(), pair.getRight());
            _output = new TransactionOutput(params, null, Coin.valueOf(spendAmount), scriptPubKey);
        }
        else    {
            Script toOutputScript = ScriptBuilder.createOutputScript(Address.fromBase58(params, strDestination));
            _output = new TransactionOutput(params, null, Coin.valueOf(spendAmount), toOutputScript.getProgram());
        }
        transaction.addOutput(_output);

        psbt = new PSBT(transaction);

        this.setStep(2);
    }

    //
    // counterparty
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