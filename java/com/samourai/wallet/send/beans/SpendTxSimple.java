package com.samourai.wallet.send.beans;

import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.utxo.UtxoOutPoint;
import org.bitcoinj.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class SpendTxSimple extends SpendTx {
    private static final Logger log = LoggerFactory.getLogger(SpendTxSimple.class);
    private Transaction tx;

    public SpendTxSimple(SpendType spendType, long amount, boolean entireBalance, long minerFee, long samouraiFee, long change, Collection<UtxoOutPoint> spendFrom, Map<String, Long> receivers, Transaction tx) throws SpendException {
        super(spendType, amount, entireBalance, minerFee, minerFee, samouraiFee, change, spendFrom, receivers, tx.getVirtualTransactionSize(), tx.getWeight(), tx.getHashAsString());
        this.tx = tx;

        if (log.isDebugEnabled()) {
            log.debug(tx.toString());
        }
    }

    public Transaction getTx() {
        return tx;
    }
}
