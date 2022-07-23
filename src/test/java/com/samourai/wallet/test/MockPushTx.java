package com.samourai.wallet.test;

import com.samourai.wallet.api.backend.IPushTx;
import com.samourai.wallet.util.TxUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedHashSet;
import java.util.Set;

public class MockPushTx implements IPushTx {

    private NetworkParameters params;
    private Set<String> txids;
    private Set<String> txRaws;

    public MockPushTx(NetworkParameters params) {
        this.params = params;
        this.txids = new LinkedHashSet<>();
        this.txRaws = new LinkedHashSet<>();
    }

    @Override
    public Pair<Boolean, String> pushTx(String hexTx) throws Exception {
        Transaction tx = TxUtil.getInstance().fromTxHex(params, hexTx);
        String txid = tx.getHashAsString();
        txids.add(txid);
        txRaws.add(hexTx);
        return Pair.of(true, tx.getHashAsString());
    }

    public void assertTx(String txid, String raw) {
        Assertions.assertTrue(hasTxid(txid));
        Assertions.assertTrue(hasTxRaw(raw));
    }

    public boolean hasTxid(String txid) {
        return txids.contains(txid);
    }

    public boolean hasTxRaw(String raw) {
        return txRaws.contains(raw);
    }

    public Set<String> getTxids() {
        return txids;
    }

    public Set<String> getTxRaws() {
        return txRaws;
    }
}
