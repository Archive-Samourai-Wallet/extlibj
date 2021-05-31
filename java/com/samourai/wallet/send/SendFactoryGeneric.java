package com.samourai.wallet.send;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bip69.BIP69MyTransactionInputComparator;
import com.samourai.wallet.bip69.BIP69OutputComparator;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.send.exceptions.TxLengthException;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.TxUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.script.ScriptOpCodes;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

//import android.util.Log;

public class SendFactoryGeneric {
    private static final Logger log = LoggerFactory.getLogger(SendFactoryGeneric.class);

    private static SendFactoryGeneric instance = null;
    public static SendFactoryGeneric getInstance() {
        if (instance == null) {
            instance = new SendFactoryGeneric();
        }
        return instance;
    }

    protected SendFactoryGeneric() { ; }

    /*
    Used by spends
     */
    public Transaction makeTransaction(Map<String, Long> receivers, List<MyTransactionOutPoint> unspent, boolean rbfOptIn, NetworkParameters params) throws Exception {

        BigInteger amount = BigInteger.ZERO;
        for(Iterator<Map.Entry<String, Long>> iterator = receivers.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Long> mapEntry = iterator.next();
            amount = amount.add(BigInteger.valueOf(mapEntry.getValue()));
        }

        List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
        Transaction tx = new Transaction(params);

        for(Iterator<Map.Entry<String, Long>> iterator = receivers.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Long> mapEntry = iterator.next();
            String toAddress = mapEntry.getKey();
            BigInteger value = BigInteger.valueOf(mapEntry.getValue());
/*
            if(value.compareTo(SamouraiWallet.bDust) < 1)    {
                throw new Exception(context.getString(R.string.dust_amount));
            }
*/
            if(value == null || (value.compareTo(BigInteger.ZERO) <= 0 && !FormatsUtilGeneric.getInstance().isValidBIP47OpReturn(toAddress))) {
                throw new Exception("Invalid amount");
            }

            TransactionOutput output = null;
            Script toOutputScript = null;
            if(!FormatsUtilGeneric.getInstance().isValidBitcoinAddress(toAddress, params) && FormatsUtilGeneric.getInstance().isValidBIP47OpReturn(toAddress))    {
                toOutputScript = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data(Hex.decode(toAddress)).build();
                output = new TransactionOutput(params, null, Coin.valueOf(0L), toOutputScript.getProgram());
            }
            else {
                output = computeTransactionOutput(toAddress, value.longValue(), params);
            }
            outputs.add(output);
        }

        List<MyTransactionInput> inputs = new ArrayList<MyTransactionInput>();
        for(MyTransactionOutPoint outPoint : unspent) {
            Script script = outPoint.computeScript();
            if(script.getScriptType() == Script.ScriptType.NO_TYPE) {
                continue;
            }

            MyTransactionInput input = new MyTransactionInput(params, null, new byte[0], outPoint, outPoint.getHash().toString(), (int)outPoint.getIndex());
            if(rbfOptIn == true)    {
                input.setSequenceNumber(SamouraiWalletConst.RBF_SEQUENCE_VAL.longValue());
            }
            inputs.add(input);
        }

        //
        // deterministically sort inputs and outputs, see BIP69 (OBPP)
        //
        Collections.sort(inputs, new BIP69MyTransactionInputComparator());
        for(TransactionInput input : inputs) {
            tx.addInput(input);
        }

        Collections.sort(outputs, new BIP69OutputComparator());
        for(TransactionOutput to : outputs) {
            tx.addOutput(to);
        }

        return tx;
    }

    public Transaction signTransaction(Transaction unsignedTx, WhirlpoolAccount account, UtxoProvider utxoProvider) throws TxLengthException {
        HashMap<String,ECKey> keyBag = new HashMap<String,ECKey>();

        for (TransactionInput input : unsignedTx.getInputs()) {

            try {
//                Log.i("SendFactory", "connected pubkey script:" + Hex.toHexString(scriptBytes));
//                Log.i("SendFactory", "address from script:" + address);
                ECKey ecKey = utxoProvider._getPrivKey(input.getOutpoint(), account);
                if(ecKey != null) {
                    keyBag.put(input.getOutpoint().toString(), ecKey);
                }
                else {
                    throw new RuntimeException("ECKey error: cannot process private key");
//                    Log.i("ECKey error", "cannot process private key");
                }
            }
            catch(Exception e) {
                log.error("signTransaction failed", e);
            }

        }

        Transaction signedTx = signTransaction(unsignedTx, keyBag);
        if(signedTx == null)    {
            return null;
        }
        else    {
            String hexString = new String(Hex.encode(signedTx.bitcoinSerialize()));
            if(hexString.length() > (100 * 1024)) {
                throw new TxLengthException();
//              Log.i("SendFactory", "Transaction length too long");
            }

            return signedTx;
        }
    }

    protected synchronized Transaction signTransaction(Transaction transaction, HashMap<String,ECKey> keyBag) throws ScriptException {
        NetworkParameters params = transaction.getParams();
        List<TransactionInput> inputs = transaction.getInputs();

        TransactionInput input = null;
        TransactionOutput connectedOutput = null;
        byte[] connectedPubKeyScript = null;
        TransactionSignature sig = null;
        Script scriptPubKey = null;
        ECKey key = null;

        for (int i = 0; i < inputs.size(); i++) {

            input = inputs.get(i);

            key = keyBag.get(input.getOutpoint().toString());
            connectedPubKeyScript = input.getOutpoint().getConnectedPubKeyScript();
            connectedOutput = input.getOutpoint().getConnectedOutput();
            scriptPubKey = connectedOutput.getScriptPubKey();

            String address;
            try {
                address = TxUtil.getInstance().getToAddress(connectedPubKeyScript, params);
            } catch (Exception e) {
                log.error("", e);
                return null;
            }

            if(FormatsUtilGeneric.getInstance().isValidBech32(address) || Address.fromBase58(params, address).isP2SHAddress())    {

                final SegwitAddress segwitAddress = new SegwitAddress(key.getPubKey(), params);
//                System.out.println("pubKey:" + Hex.toHexString(key.getPubKey()));
//                final Script scriptPubKey = p2shp2wpkh.segWitOutputScript();
//                System.out.println("scriptPubKey:" + Hex.toHexString(scriptPubKey.getProgram()));
//                System.out.println("to address from script:" + scriptPubKey.getToAddress(params).toString());
                final Script redeemScript = segwitAddress.segWitRedeemScript();
//                System.out.println("redeem script:" + Hex.toHexString(redeemScript.getProgram()));
                final Script scriptCode = redeemScript.scriptCode();
//                System.out.println("script code:" + Hex.toHexString(scriptCode.getProgram()));

                sig = transaction.calculateWitnessSignature(i, key, scriptCode, connectedOutput.getValue(), Transaction.SigHash.ALL, false);
                final TransactionWitness witness = new TransactionWitness(2);
                witness.setPush(0, sig.encodeToBitcoin());
                witness.setPush(1, key.getPubKey());
                transaction.setWitness(i, witness);

                if(!FormatsUtilGeneric.getInstance().isValidBech32(address) && Address.fromBase58(params, address).isP2SHAddress())    {
                    final ScriptBuilder sigScript = new ScriptBuilder();
                    sigScript.data(redeemScript.getProgram());
                    transaction.getInput(i).setScriptSig(sigScript.build());
                    transaction.getInput(i).getScriptSig().correctlySpends(transaction, i, scriptPubKey, connectedOutput.getValue(), Script.ALL_VERIFY_FLAGS);
                }

            }
            else    {
                if(key != null && key.hasPrivKey() || key.isEncrypted()) {
                    sig = transaction.calculateSignature(i, key, connectedPubKeyScript, Transaction.SigHash.ALL, false);
                }
                else {
                    sig = TransactionSignature.dummy();   // watch only ?
                }

                if(scriptPubKey.isSentToAddress()) {
                    input.setScriptSig(ScriptBuilder.createInputScript(sig, key));
                }
                else if(scriptPubKey.isSentToRawPubKey()) {
                    input.setScriptSig(ScriptBuilder.createInputScript(sig));
                }
                else {
                    throw new RuntimeException("Unknown script type: " + scriptPubKey);
                }
            }

        }

        return transaction;

    }

    TransactionOutput computeTransactionOutput(String address, long amount, NetworkParameters params) throws Exception {
        if(FormatsUtilGeneric.getInstance().isValidBech32(address))    {
            return Bech32UtilGeneric.getInstance().getTransactionOutput(address, amount, params);
        }
        else    {
            Script outputScript = ScriptBuilder.createOutputScript(org.bitcoinj.core.Address.fromBase58(params, address));
            return new TransactionOutput(params, null, Coin.valueOf(amount), outputScript.getProgram());
        }
    }

}
