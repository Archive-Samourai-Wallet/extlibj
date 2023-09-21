package com.samourai.wallet.utxo;

public class UtxoDetailImpl extends UtxoRefImpl implements UtxoDetail {
    private long value;
    private String address;
    private Integer confirmedBlockHeight; // null when unconfirmed

    public UtxoDetailImpl(String txHash, int txOutputIndex, long value, String address, Integer confirmedBlockHeight) {
        super(txHash, txOutputIndex);
        this.value = value;
        this.address = address;
        this.confirmedBlockHeight = confirmedBlockHeight;
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
        return latestBlockHeight-confirmedBlockHeight;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", value=" + value +
                ", address='" + address + '\'' +
                ", confirmedBlockHeight=" + confirmedBlockHeight;
    }
}
