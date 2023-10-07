package com.samourai.wallet.send;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.send.exceptions.MakeTxException;
import com.samourai.wallet.send.exceptions.SignTxException;
import com.samourai.wallet.send.exceptions.SignTxLengthException;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.TxUtil;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.InputOutPoint;
import com.samourai.wallet.utxo.UtxoOutPoint;
import org.bitcoinj.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

//import android.util.Log;

public class SendFactoryGeneric {
    private static final Logger log = LoggerFactory.getLogger(SendFactoryGeneric.class);
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
    private static final TxUtil txUtil = TxUtil.getInstance();

    private static SendFactoryGeneric instance = null;
    public static SendFactoryGeneric getInstance() {
        if (instance == null) {
            instance = new SendFactoryGeneric();
        }
        return instance;
    }

    protected SendFactoryGeneric() { ; }

    // used by android
    public Transaction makeTransaction(Collection<UtxoOutPoint> unspent, Map<String, BigInteger> receivers, BipFormatSupplier bipFormatSupplier, boolean rbfOptIn, NetworkParameters params, long blockHeight) throws MakeTxException {
        Map<String, Long> receiversLong = new LinkedHashMap<>();
        for (Map.Entry<String,BigInteger> entry : receivers.entrySet()) {
            receiversLong.put(entry.getKey(), entry.getValue().longValue());
        }
        return makeTransaction(receiversLong, unspent, bipFormatSupplier, rbfOptIn, params, blockHeight);
    }

    /*
    Used by spends
     */
    public Transaction makeTransaction(Map<String, Long> receivers, Collection<UtxoOutPoint> unspent, BipFormatSupplier bipFormatSupplier, boolean rbfOptIn, NetworkParameters params, long blockHeight) throws MakeTxException {

        Transaction tx = new Transaction(params);
        if(receivers.size() == 4 && blockHeight > 0L)    {
          tx.setVersion(2);
          tx.setLockTime(blockHeight);
          rbfOptIn = true;
        }

        List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

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
                throw new MakeTxException("Invalid amount");
            }

            try {
                TransactionOutput output = bipFormatSupplier.getTransactionOutput(toAddress, value.longValue(), params);
                outputs.add(output);
            } catch (Exception e) {
                log.error("getTransactionOutput failed", e);
                throw new MakeTxException(e);
            }
        }

        List<TransactionInput> inputs = new ArrayList<>();
        for(UtxoOutPoint outPoint : unspent) {
            // check outpoint format
            try {
                bipFormatSupplier.getToAddress(outPoint.getScriptBytes(), params);
                // ok, outpoint format is supported
            } catch (Exception e) {
                // outpoint format is not supported, skip it
                log.error("skipping outPoint (unsupported type): "+outPoint);
                continue;
            }

            TransactionInput input = utxoUtil.computeSpendInput(outPoint, params);
            if (rbfOptIn) {
                input.setSequenceNumber(SamouraiWalletConst.RBF_SEQUENCE_VAL.longValue());
            }
            inputs.add(input);
        }

        if (inputs.isEmpty()) {
            throw new MakeTxException("TX has no inputs");
        }
        if (outputs.isEmpty()) {
            throw new MakeTxException("TX has no outputs");
        }

        //
        // deterministically sort inputs and outputs, see BIP69 (OBPP)
        //
        for(TransactionInput input : inputs) {
            tx.addInput(input);
        }
        for(TransactionOutput to : outputs) {
            tx.addOutput(to);
        }
        txUtil.sortBip69InputsAndOutputs(tx);
        return tx;
    }

    // used by Android
    public Transaction signTransaction(Transaction transaction, Map<String,ECKey> keyBag) throws SignTxException {
        return signTransaction(transaction, keyBag, BIP_FORMAT.PROVIDER);
    }

    public Transaction signTransaction(Transaction transaction, KeyBag keyBag) throws SignTxException {
        return signTransaction(transaction, keyBag.toMap());
    }

    public Transaction signTransaction(Transaction transaction, Map<String,ECKey> keyBag, BipFormatSupplier bipFormatSupplier) throws SignTxException {
        return signTransaction(transaction, keyBag, bipFormatSupplier, null);
    }

    public Transaction signTransaction(Transaction transaction, KeyBag keyBag, BipFormatSupplier bipFormatSupplier) throws SignTxException {
        return signTransaction(transaction, keyBag.toMap(), bipFormatSupplier);
    }

    public synchronized Transaction signTransaction(Transaction transaction, Map<String,ECKey> keyBag, BipFormatSupplier bipFormatSupplier, Function<TransactionOutPoint, InputOutPoint> getInputOutPointOrNull) throws SignTxException {
        int nbSigned = 0;
        for (int i = 0; i < transaction.getInputs().size(); i++) {
            TransactionInput input = transaction.getInput(i);
            ECKey key = keyBag.get(utxoUtil.utxoToKey(input.getOutpoint()));
            if (key != null) {
                try {
                    // read input info from provided outPoint when available
                    InputOutPoint outPoint = getInputOutPointOrNull != null ? getInputOutPointOrNull.apply(input.getOutpoint()) : null;
                    this.signInput(key, transaction, i, bipFormatSupplier, outPoint);
                    nbSigned++;
                } catch (Exception e) {
                    log.error("Signing input #" + i + " failed", e);
                    throw new SignTxException("Signing input #" + i + " failed", e);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Signed "+nbSigned+"/"+transaction.getInputs().size()+" inputs");
        }
        verifySignedTx(transaction);
        return transaction;
    }

    public synchronized Transaction signTransaction(Transaction transaction, KeyBag keyBag, BipFormatSupplier bipFormatSupplier, Function<TransactionOutPoint,InputOutPoint> getInputOutPoint) throws SignTxException {
        return signTransaction(transaction, keyBag.toMap(), bipFormatSupplier, getInputOutPoint);
    }

    protected void verifySignedTx(Transaction transaction) throws SignTxException {
        transaction.verify();

        String hexString = TxUtil.getInstance().getTxHex(transaction);
        if(hexString.length() > (100 * 1024)) {
            log.warn("Transaction length too long: txLength="+hexString.length());
            throw new SignTxLengthException();
//              Log.i("SendFactory", "Transaction length too long");
        }
    }

    public void signInput(ECKey key, Transaction tx, int inputIndex, BipFormatSupplier bipFormatSupplier, InputOutPoint inputOutPointOrNull) throws Exception {
        TransactionInput txInput = tx.getInput(inputIndex);
        if (key == null) {
            throw new Exception("No key found for signing input: "+txInput);
        }

        String inputAddress;
        if (inputOutPointOrNull != null) {
            // read input info from provided outPoint
            inputAddress = bipFormatSupplier.getToAddress(inputOutPointOrNull.getScriptBytes(), tx.getParams());

            // set input value from provided outpoint
            txInput.setValue(Coin.valueOf(inputOutPointOrNull.getValueLong()));
        } else {
            // read input info directly from txInput.connectedOutput
            // this will only work for transactions build interactively, not when imported from raw (ie Cahoots)
            // for such txs, you need to provide your own outPoint
            TransactionOutput connectedOutput = txInput.getOutpoint().getConnectedOutput();
            if (connectedOutput == null) {
                throw new Exception("Signing input #" + inputIndex + " failed: connectedOutput is null. Try providing getInputOutPoint argument for SendFactoryGeneric.signTransaction() when signing a tx built from raw");
            }
            inputAddress = bipFormatSupplier.getToAddress(connectedOutput);
        }

        BipFormat addressFormat = bipFormatSupplier.findByAddress(inputAddress, tx.getParams());

        if (log.isDebugEnabled()) {
            log.debug("signInput #"+inputIndex+": value="+txInput.getValue()+", addressType="+addressFormat+", address="+inputAddress);
        }
        addressFormat.sign(tx, inputIndex, key);
    }

    public Transaction signTransactionForSweep(Transaction unsignedTx, ECKey privKey, NetworkParameters params) throws SignTxException    {
        HashMap<String,ECKey> keyBag = new HashMap<>();
        for (TransactionInput input : unsignedTx.getInputs()) {
            try {
                DumpedPrivateKey pk = new DumpedPrivateKey(params, privKey.getPrivateKeyAsWiF(params));
                ECKey ecKey = pk.getKey();
                keyBag.put(input.getOutpoint().toString(), ecKey);
            }
            catch(Exception e) {
                log.error("cannot process private key for input="+input, e);
            }
        }

        Transaction signedTx = signTransaction(unsignedTx, keyBag);
        return signedTx;
    }

}
