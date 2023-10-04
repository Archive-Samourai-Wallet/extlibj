package com.samourai.wallet.utxo;

import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtxoDetailImpl extends UtxoRefImpl implements UtxoDetail {
    protected static final Logger log = LoggerFactory.getLogger(UtxoDetailImpl.class);

    private long value;
    private String address;
    private Integer confirmedBlockHeight; // null when unconfirmed
    private NetworkParameters params;

    public UtxoDetailImpl(String txHash, int txOutputIndex, long value, String address, Integer confirmedBlockHeight, NetworkParameters params) {
        super(txHash, txOutputIndex);
        this.value = value;
        this.address = address;
        this.confirmedBlockHeight = confirmedBlockHeight;
        this.params = params;
    }

    public UtxoDetailImpl(UtxoDetail utxoDetail) {
        super(utxoDetail);
        this.value = utxoDetail.getValue();
        this.address = utxoDetail.getAddress();
        this.confirmedBlockHeight = utxoDetail.getConfirmedBlockHeight();
        this.params = utxoDetail.getParams();
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public Integer getConfirmedBlockHeight() {
        return confirmedBlockHeight;
    }

    @Override
    public void setConfirmedBlockHeight(Integer confirmedBlockHeight) {
        this.confirmedBlockHeight = confirmedBlockHeight;
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
    public NetworkParameters getParams() {
        return params;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", value=" + value +
                ", address='" + address + '\'' +
                ", confirmedBlockHeight=" + confirmedBlockHeight;
    }
}
