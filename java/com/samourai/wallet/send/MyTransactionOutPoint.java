package com.samourai.wallet.send;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.utxo.InputOutPoint;
import com.samourai.wallet.utxo.UtxoConfirmInfo;
import com.samourai.wallet.utxo.UtxoOutPoint;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Collection;

public class MyTransactionOutPoint extends TransactionOutPoint implements UtxoOutPoint, InputOutPoint {

    private static final long serialVersionUID = 1L;
    private byte[] scriptBytes;
    private BigInteger value;
    private String address;
    private int confirmations;

    public MyTransactionOutPoint(NetworkParameters params, Sha256Hash txHash, int txOutputN, BigInteger value, byte[] scriptBytes, String address, int confirmations) throws ProtocolException {
        super(params, txOutputN, new Sha256Hash(txHash.getBytes()));
        this.scriptBytes = scriptBytes;
        this.value = value;
        this.address = address;
        this.confirmations = confirmations;
    }

    public MyTransactionOutPoint(TransactionOutput txOutput, String address, int confirmations) {
        this(txOutput.getParams(), txOutput.getParentTransactionHash(), txOutput.getIndex(), BigInteger.valueOf(txOutput.getValue().getValue()), txOutput.getScriptBytes(), address, confirmations);
    }

    public MyTransactionOutPoint(UtxoOutPoint o, NetworkParameters params, int confirmations) {
        this(params, Sha256Hash.wrap(Hex.decode(o.getTxHash())), o.getTxOutputIndex(), BigInteger.valueOf(o.getValueLong()), o.getScriptBytes(), o.getAddress(), confirmations);
    }

    public MyTransactionOutPoint(UnspentOutput o, NetworkParameters params) {
        this(params, Sha256Hash.wrap(Hex.decode(o.getTxHash())), o.getTxOutputIndex(), BigInteger.valueOf(o.getValueLong()), o.getScriptBytes(), o.getAddress(), o.confirmations);
    }

    public static long sumValue(Collection<MyTransactionOutPoint> outpoints) {
        return outpoints.stream().mapToLong(utxo -> utxo.getValue().getValue()).sum();
    }

    public int getConfirmations() {
        return confirmations;
    }

    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    public Coin getValue() {
        return Coin.valueOf(value.longValue());
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public UtxoConfirmInfo getConfirmInfo() {
        return null;
    }

    @Override
    public void setConfirmInfo(UtxoConfirmInfo confirmInfo) {

    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public Script computeScript() {
        return new Script(scriptBytes);
    }

    @Override
    public TransactionOutput getConnectedOutput() {
        return new TransactionOutput(params, null, Coin.valueOf(value.longValue()), scriptBytes);
    }

    @Override
    public byte[] getConnectedPubKeyScript() {
        return scriptBytes;
    }

    @Override
    public String toString() {
        return "utxo="+super.toString()+
                ", value=" + value +
                ", address='" + address + '\'';
    }

    // implement UtxoOutPoint

    @Override
    public long getValueLong() {
        return getValue().getValue();
    }

    @Override
    public String getTxHash() {
        return getHash().toString();
    }

    @Override
    public int getTxOutputIndex() {
        return (int)getIndex();
    }

}