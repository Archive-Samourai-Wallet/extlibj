package com.samourai.wallet.utxo;

public class UtxoRefImpl implements UtxoRef {
    private String txHash;
    private int txOutputIndex;

    public UtxoRefImpl(String txHash, int txOutputIndex) {
        this.txHash = txHash;
        this.txOutputIndex = txOutputIndex;
    }

    public UtxoRefImpl(UtxoRef utxoRef) {
        this.txHash = utxoRef.getTxHash();
        this.txOutputIndex = utxoRef.getTxOutputIndex();
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
