package com.samourai.wallet.utxo;

import java.util.Collection;

public interface UtxoOutPoint extends UtxoDetail, InputOutPoint {
    byte[] getScriptBytes();

    static long sumValue(Collection<? extends UtxoOutPoint> utxos) {
        return utxos.stream().mapToLong(utxo -> utxo.getValueLong()).sum();
    }
}
