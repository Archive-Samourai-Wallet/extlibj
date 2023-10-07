package com.samourai.wallet.utxo;

public class UtxoDetailImpl extends UtxoRefImpl implements UtxoDetail {
    private long value;
    private String address;
    private UtxoConfirmInfo confirmInfo;

    public UtxoDetailImpl(String txHash, int txOutputIndex, long value, String address, UtxoConfirmInfo confirmInfo) {
        super(txHash, txOutputIndex);
        this.value = value;
        this.address = address;
        this.confirmInfo = confirmInfo != null ? confirmInfo : new UtxoConfirmInfoImpl((Integer)null);
    }

    public UtxoDetailImpl(UtxoDetail utxoDetail) {
        super(utxoDetail);
        this.value = utxoDetail.getValueLong();
        this.address = utxoDetail.getAddress();
        this.confirmInfo = new UtxoConfirmInfoImpl(utxoDetail.getConfirmInfo());
    }

    @Override
    public long getValueLong() {
        return value;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public UtxoConfirmInfo getConfirmInfo() {
        return confirmInfo;
    }

    @Override
    public void setConfirmInfo(UtxoConfirmInfo confirmInfo) {
        this.confirmInfo = confirmInfo;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", value=" + value +
                ", address='" + address + '\'' +
                ", confirmInfo=" + (confirmInfo!=null?"{"+confirmInfo+"}":"null");
    }
}
