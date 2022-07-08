package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.send.MyTransactionOutPoint;
import org.bitcoinj.core.TransactionInput;

import java.util.List;

public class Stonewallx2InputData {
    private final long contributedAmount;
    private final List<CahootsUtxo> utxos;
    private final List<TransactionInput> inputs;
    public Stonewallx2InputData(long contributedAmount, List<CahootsUtxo> utxos, List<TransactionInput> inputs) {
        this.contributedAmount = contributedAmount;
        this.utxos = utxos;
        this.inputs = inputs;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public List<CahootsUtxo> getUtxos() {
        return utxos;
    }

    public long getContributedAmount() {
        return contributedAmount;
    }
}
