package com.samourai.wallet.utxo;

public interface UtxoRef {
    String getTxHash();
    int getTxOutputIndex();
}
