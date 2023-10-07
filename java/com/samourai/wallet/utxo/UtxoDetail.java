package com.samourai.wallet.utxo;

import java.util.Collection;

public interface UtxoDetail extends UtxoRef {
    long getValueLong();
    String getAddress();
    UtxoConfirmInfo getConfirmInfo();
    void setConfirmInfo(UtxoConfirmInfo confirmInfo);

    static long sumValue(Collection<? extends UtxoDetail> utxos) {
        return utxos.stream().mapToLong(utxo -> utxo.getValueLong()).sum();
    }
}
