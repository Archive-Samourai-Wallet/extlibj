package com.samourai.wallet.utxo;

import com.samourai.wallet.send.MyTransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;

import java.math.BigInteger;

public class UtxoOutPointImpl extends UtxoDetailImpl implements UtxoOutPoint {
    private byte[] scriptBytes;

    public UtxoOutPointImpl(String txHash, int txOutputIndex, long value, String address, UtxoConfirmInfo confirmInfo, byte[] scriptBytes) {
        super(txHash, txOutputIndex, value, address, confirmInfo);
        this.scriptBytes = scriptBytes;
    }

    public UtxoOutPointImpl(UtxoOutPoint utxoOutPoint) {
        super(utxoOutPoint);
        this.scriptBytes = utxoOutPoint.getScriptBytes();
    }

    public UtxoOutPointImpl(MyTransactionOutPoint o) {
        this(o.getHash().toString(), (int)o.getIndex(), o.getValue().getValue(), o.getAddress(), null, o.getScriptBytes());
    }

    public UtxoOutPointImpl(TransactionOutput txOutput, String address) {
        this(txOutput.getParentTransactionHash().toString(), txOutput.getIndex(), txOutput.getValue().getValue(), address, null, txOutput.getScriptBytes());
    }

    @Override
    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", scriptBytes=" + scriptBytes.length+" bytes";
    }
}
