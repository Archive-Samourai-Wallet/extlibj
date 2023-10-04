package com.samourai.wallet.utxo;

import com.samourai.wallet.hd.Chain;
import org.bitcoinj.core.NetworkParameters;

import java.util.Collection;

public interface UtxoDetail extends UtxoRef {
    long getValue();
    String getAddress();
    Integer getConfirmedBlockHeight();
    void setConfirmedBlockHeight(Integer confirmedBlockHeight);
    boolean isConfirmed();
    int getConfirmations(int latestBlockHeight);
    NetworkParameters getParams();

    static long sumValue(Collection<? extends UtxoDetail> utxos) {
        return utxos.stream().mapToLong(utxo -> utxo.getValue()).sum();
    }
}
