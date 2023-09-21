package com.samourai.wallet.utxo;

public class UtxoRefImpl implements UtxoRef {
    private String txHash;
    private int txOutputIndex;

    public UtxoRefImpl(String txHash, int txOutputIndex) {
        this.txHash = txHash;
        this.txOutputIndex = txOutputIndex;
    }

    @Override
    public String getTxHash() {
        return txHash;
    }

    @Override
    public int getTxOutputIndex() {
        return txOutputIndex;
    }

    @Override
    public String toString() {
        return "utxo="+txHash + ':' + txOutputIndex;
    }
}
