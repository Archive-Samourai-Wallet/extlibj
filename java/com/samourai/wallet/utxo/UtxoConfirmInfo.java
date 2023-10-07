package com.samourai.wallet.utxo;

public interface UtxoConfirmInfo {
    Integer getConfirmedBlockHeight();
    boolean isConfirmed();
    int getConfirmations(int latestBlockHeight);
}
