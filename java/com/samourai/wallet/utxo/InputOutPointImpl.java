package com.samourai.wallet.utxo;

import org.bitcoinj.core.TransactionOutput;

public class InputOutPointImpl implements InputOutPoint {
    private long valueLong;
    private byte[] scriptBytes;

    public InputOutPointImpl(long valueLong, byte[] scriptBytes) {
        this.valueLong = valueLong;
        this.scriptBytes = scriptBytes;
    }

    public InputOutPointImpl(TransactionOutput inputConnectedOutput) {
        this.valueLong = inputConnectedOutput.getValue().getValue();
        this.scriptBytes = inputConnectedOutput.getScriptBytes();
    }

    @Override
    public long getValueLong() {
        return valueLong;
    }

    @Override
    public byte[] getScriptBytes() {
        return scriptBytes;
    }
}
