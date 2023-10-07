package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.cahoots.psbt.PSBT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CahootsResultImpl<C extends CahootsContext,T extends Cahoots<C>> implements CahootsResult<C,T> {
    private static final Logger log = LoggerFactory.getLogger(CahootsResultImpl.class);
    private C cahootsContext;
    private T cahoots;
    private long spendAmount;
    private long feeAmount;
    private String destination;
    private String paynymDestination;
    private PSBT psbt;

    public CahootsResultImpl(C cahootsContext, T cahoots, long spendAmount, long feeAmount, String destination, String paynymDestination, PSBT psbt) {
        this.cahootsContext = cahootsContext;
        this.cahoots = cahoots;
        this.spendAmount = spendAmount;
        this.feeAmount = feeAmount;
        this.destination = destination;
        this.paynymDestination = paynymDestination;
        this.psbt = psbt;
    }

    @Override
    public C getCahootsContext() {
        return cahootsContext;
    }

    @Override
    public T getCahoots() {
        return cahoots;
    }

    @Override
    public long getSpendAmount() {
        return spendAmount;
    }

    @Override
    public long getFeeAmount() {
        return feeAmount;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public String getPaynymDestination() {
        return paynymDestination;
    }

    @Override
    public PSBT getPsbt() {
        return psbt;
    }
}
