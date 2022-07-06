package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.send.MyTransactionOutPoint;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;

public class Stonewallx2InputData {
    private final long contributedAmount;
    private final List<CahootsUtxo> utxos;
    private final HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputs;
    public Stonewallx2InputData(long contributedAmount, List<CahootsUtxo> utxos, HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> inputs) {
        this.contributedAmount = contributedAmount;
        this.utxos = utxos;
        this.inputs = inputs;
    }

    public HashMap<MyTransactionOutPoint, Triple<byte[], byte[], String>> getInputs() {
        return inputs;
    }

    public List<CahootsUtxo> getUtxos() {
        return utxos;
    }

    public long getContributedAmount() {
        return contributedAmount;
    }
}
