package com.samourai.wallet.send.beans;

import com.samourai.wallet.bipFormat.BipFormat;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SpendTx {
    private static final Logger log = LoggerFactory.getLogger(SpendTx.class);
    private SpendType spendType;
    private BipFormat changeFormat;
    private long amount;
    private long fee;
    private long change;
    private List<? extends TransactionOutPoint> spendFrom;
    private Map<String, Long> receivers;
    private Transaction tx;

    public SpendTx(SpendType spendType, BipFormat changeFormat, long amount, long fee, long change, List<? extends TransactionOutPoint> spendFrom, Map<String, Long> receivers, Transaction tx) {
        this.spendType = spendType;
        this.changeFormat = changeFormat;
        this.amount = amount;
        this.fee = fee;
        this.spendFrom = spendFrom;
        this.receivers = receivers;
        this.change = change;
        this.tx = tx;
    }

    public SpendType getSpendType() {
        return spendType;
    }

    public BipFormat getChangeFormat() {
        return changeFormat;
    }

    public long getAmount() {
        return amount;
    }

    public long getFee() {
        return fee;
    }

    public long getChange() {
        return change;
    }

    public Map<String, Long> getSpendTo() {
        return receivers;
    }

    public List<? extends TransactionOutPoint> getSpendFrom() {
        return spendFrom;
    }

    public Transaction getTx() {
        return tx;
    }
}
