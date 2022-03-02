package com.samourai.wallet.send;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bip69.BIP69InputComparator;
import com.samourai.wallet.bip69.BIP69OutputComparator;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.send.exceptions.MakeTxException;
import com.samourai.wallet.send.exceptions.SignTxException;
import com.samourai.wallet.send.exceptions.SignTxLengthException;
import com.samourai.wallet.send.provider.UtxoKeyProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.TxUtil;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
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

    // used by android
    public Transaction makeTransaction(List<MyTransactionOutPoint> unspent, Map<String, BigInteger> receivers, boolean rbfOptIn, NetworkParameters params, long blockHeight) throws MakeTxException {
        Map<String, Long> receiversLong = new LinkedHashMap<>();
        for (Map.Entry<String,BigInteger> entry : receivers.entrySet()) {
            receiversLong.put(entry.getKey(), entry.getValue().longValue());
        }
        return makeTransaction(receiversLong, unspent, rbfOptIn, params, blockHeight);
    }

    /*
    Used by spends
     */
    public Transaction makeTransaction(Map<String, Long> receivers, List<MyTransactionOutPoint> unspent, boolean rbfOptIn, NetworkParameters params, long blockHeight) throws MakeTxException {

        BigInteger amount = BigInteger.ZERO;
        for(Iterator<Map.Entry<String, Long>> iterator = receivers.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Long> mapEntry = iterator.next();
            amount = amount.add(BigInteger.valueOf(mapEntry.getValue()));
        }

        Transaction tx = new Transaction(params);
        tx.setVersion(2);
        if(rbfOptIn)    {
            if(blockHeight > 0L)    {
                tx.setLockTime(blockHeight);
            }
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
                TransactionOutput output = TxUtil.getInstance().computeTransactionOutput(toAddress, value.longValue(), params);
                outputs.add(output);
            } catch (Exception e) {
                log.error("computeTransactionOutput failed", e);
                throw new MakeTxException(e);
            }
        }

        List<TransactionInput> inputs = new ArrayList<>();
        for(MyTransactionOutPoint outPoint : unspent) {
            Script script = outPoint.computeScript();
            if(script.getScriptType() == Script.ScriptType.NO_TYPE) {
                continue;
            }

            TransactionInput input = outPoint.computeSpendInput();
            if(rbfOptIn)    {
                input.setSequenceNumber(SamouraiWalletConst.RBF_SEQUENCE_VAL.longValue());
            }
            inputs.add(input);
        }

        //
        // deterministically sort inputs and outputs, see BIP69 (OBPP)
        //
        Collections.sort(inputs, new BIP69InputComparator());
        for(TransactionInput input : inputs) {
            tx.addInput(input);
        }

        Collections.sort(outputs, new BIP69OutputComparator());
        for(TransactionOutput to : outputs) {
            tx.addOutput(to);
        }

        return tx;
    }

    public Transaction signTransaction(Transaction unsignedTx, UtxoKeyProvider utxoProvider) throws SignTxException {
        HashMap<String,ECKey> keyBag = new HashMap<String,ECKey>();
        for (TransactionInput input : unsignedTx.getInputs()) {
            try {
//                Log.i("SendFactory", "connected pubkey script:" + Hex.toHexString(scriptBytes));
//                Log.i("SendFactory", "address from script:" + address);
                String hash = input.getOutpoint().getHash().toString();
                int index = (int)input.getOutpoint().getIndex();
                ECKey ecKey = utxoProvider._getPrivKey(hash, index);
                if(ecKey == null) {
                    throw new Exception("Key not found for input: "+hash+":"+index);
                }
                keyBag.put(input.getOutpoint().toString(), ecKey);
            }
            catch(Exception e) {
                throw new SignTxException(e);
            }
        }

        Transaction signedTx = signTransaction(unsignedTx, keyBag, utxoProvider.getBipFormatSupplier());
        if(signedTx == null)    {
            return null;
        }
        else    {
            String hexString = TxUtil.getInstance().getTxHex(signedTx);
            if(hexString.length() > (100 * 1024)) {
                log.warn("Transaction length too long: txLength="+hexString.length());
                throw new SignTxLengthException();
//              Log.i("SendFactory", "Transaction length too long");
            }

            return signedTx;
        }
    }

    // used by Android
    public Transaction signTransaction(Transaction transaction, Map<String,ECKey> keyBag) throws SignTxException {
        return signTransaction(transaction, keyBag, BIP_FORMAT.PROVIDER);
    }

    public synchronized Transaction signTransaction(Transaction transaction, Map<String,ECKey> keyBag, BipFormatSupplier bipFormatSupplier) throws SignTxException {
        List<TransactionInput> inputs = transaction.getInputs();

        for (int i = 0; i < inputs.size(); i++) {
            TransactionInput input = transaction.getInput(i);
            ECKey key = keyBag.get(input.getOutpoint().toString());
            try {
                this.signInput(key, transaction, i, bipFormatSupplier);
            } catch (Exception e) {
                log.error("Signing input #"+i+" failed", e);
                throw new SignTxException("Signing input #"+i+" failed", e);
            }
        }
        return transaction;
    }

    public void signInput(ECKey key, Transaction tx, int inputIndex, BipFormatSupplier bipFormatSupplier) throws Exception {
        if (key == null) {
            throw new Exception("No key found for signing input #"+inputIndex);
        }

        // sign input
        TransactionInput txInput = tx.getInput(inputIndex);
        TransactionOutput connectedOutput = txInput.getOutpoint().getConnectedOutput();
        String inputAddress = TxUtil.getInstance().getToAddress(connectedOutput);
        BipFormat addressFormat = bipFormatSupplier.findByAddress(inputAddress, tx.getParams());

        if (log.isDebugEnabled()) {
            log.debug("signInput #"+inputIndex+": value="+txInput.getValue()+", addressType="+addressFormat+", address="+inputAddress);
        }
        addressFormat.sign(tx, inputIndex, key);
    }

}
