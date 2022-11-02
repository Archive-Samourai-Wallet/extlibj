package com.samourai.soroban.cahoots;

import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StowawayContext extends CahootsContext {
    private static final Logger log = LoggerFactory.getLogger(StowawayContext.class);

    protected StowawayContext(CahootsTypeUser typeUser, int account, Long feePerB, Long amount) {
        super(typeUser, CahootsType.STOWAWAY, account, feePerB, amount, null);
    }

    public static StowawayContext newInitiator(int account, long feePerB, long amount) {
        return new StowawayContext(CahootsTypeUser.SENDER, account, feePerB, amount);
    }

    public static StowawayContext newCounterparty(int account) {
        // force account #0 for Stowaway counterparty (MULTI uses newCounterpartyMulti() to bypass it)
        account = 0;
        return new StowawayContext(CahootsTypeUser.COUNTERPARTY, account, null,null);
    }

    public static StowawayContext newCounterpartyMulti(int account) {
        // allow non-zero account for Stowaway MULTI
        return new StowawayContext(CahootsTypeUser.COUNTERPARTY, account, null,null);
    }
}
