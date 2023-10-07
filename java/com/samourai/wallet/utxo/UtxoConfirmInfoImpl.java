package com.samourai.wallet.utxo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtxoConfirmInfoImpl implements UtxoConfirmInfo {
    protected static final Logger log = LoggerFactory.getLogger(UtxoDetailImpl.class);
    private Integer confirmedBlockHeight; // null when unconfirmed

    public UtxoConfirmInfoImpl(Integer confirmedBlockHeight) {
        this.confirmedBlockHeight = confirmedBlockHeight;
    }

    public UtxoConfirmInfoImpl(UtxoConfirmInfo utxoConfirmInfo) {
        this.confirmedBlockHeight = utxoConfirmInfo!=null?utxoConfirmInfo.getConfirmedBlockHeight():null;
    }

    @Override
    public Integer getConfirmedBlockHeight() {
        return confirmedBlockHeight;
    }

    @Override
    public boolean isConfirmed() {
        return confirmedBlockHeight != null;
    }

    @Override
    public int getConfirmations(int latestBlockHeight) {
        if (confirmedBlockHeight == null) {
            log.warn("getConfirmations() failed: confirmedBlockHeight=null");
            return 0;
        }
        return latestBlockHeight-confirmedBlockHeight;
    }

    @Override
    public String toString() {
        return "confirmedBlockHeight=" + (confirmedBlockHeight!=null?confirmedBlockHeight:"null");
    }
}
