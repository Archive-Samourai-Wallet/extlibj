package com.samourai.wallet.utxo;

import java.util.Collection;

public interface UtxoDetail extends UtxoRef {
    long getValue();
    String getAddress();
    Integer getConfirmedBlockHeight();
    void setConfirmedBlockHeight(Integer confirmedBlockHeight);
    boolean isConfirmed();
    int getConfirmations(int latestBlockHeight);

    static long sumValue(Collection<? extends UtxoDetail> utxos) {
        long sumValue = 0;
        for (UtxoDetail utxo : utxos) {
            sumValue += utxo.getValue();
        }
        return sumValue;
    }
}
