package com.samourai.wallet.utxo;

// info required to sign a transaction input
public interface InputOutPoint {
    byte[] getScriptBytes();
    long getValueLong();
}
