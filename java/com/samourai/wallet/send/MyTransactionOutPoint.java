package com.samourai.wallet.send;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;

import java.math.BigInteger;

public class MyTransactionOutPoint extends TransactionOutPoint {

    private static final long serialVersionUID = 1L;
    private byte[] scriptBytes;
    private BigInteger value;
    private int confirmations;
    private String address;
    private boolean isChange = false;

    public MyTransactionOutPoint(NetworkParameters params, Sha256Hash txHash, int txOutputN, BigInteger value, byte[] scriptBytes, String address) throws ProtocolException {
        super(params, txOutputN, new Sha256Hash(txHash.getBytes()));
        this.scriptBytes = scriptBytes;
        this.value = value;
        this.address = address;
    }

    public MyTransactionOutPoint(NetworkParameters params, UnspentOutput out) {
        this(params, new Sha256Hash(out.tx_hash), out.tx_output_n, BigInteger.valueOf(out.value), out.getScriptBytes(), out.addr);
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

    public String getAddress() {
        return address;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setIsChange(boolean isChange) {
        this.isChange = isChange;
    }

    public Script computeScript() {
        return new Script(scriptBytes);
    }

    @Override
    public TransactionOutput getConnectedOutput() {
        return new TransactionOutput(params, null, Coin.valueOf(value.longValue()), scriptBytes);
    }

    //@Override
    public byte[] getConnectedPubKeyScript() {
        return scriptBytes;
    }
}