package com.samourai.soroban.cahoots;

import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stonewallx2Context extends CahootsContext {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2Context.class);

    protected Stonewallx2Context(CahootsTypeUser typeUser, int account, Long feePerB, Long amount, String address) {
        super(typeUser, CahootsType.STONEWALLX2, account, feePerB, amount, address);
    }

    public static Stonewallx2Context newInitiator(int account, long feePerB, long amount, String address) {
        return new Stonewallx2Context(CahootsTypeUser.SENDER, account, feePerB, amount, address);
    }

    public static Stonewallx2Context newCounterparty(int account) {
        return new Stonewallx2Context(CahootsTypeUser.COUNTERPARTY, account, null,null, null);
    }
}
