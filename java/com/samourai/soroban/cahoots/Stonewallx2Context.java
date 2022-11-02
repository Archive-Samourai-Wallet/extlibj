package com.samourai.soroban.cahoots;

import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stonewallx2Context extends CahootsContext {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2Context.class);

    // only set for initiator, when sending to paynym
    // android will check STONEWALLx2.getPaynymDestination() to increment paynym counter after successfull broadcast
    private String paynymDestination;

    protected Stonewallx2Context(CahootsTypeUser typeUser, int account, Long feePerB, Long amount, String address, String paynymDestination) {
        super(typeUser, CahootsType.STONEWALLX2, account, feePerB, amount, address);
        this.paynymDestination = paynymDestination;
    }

    public static Stonewallx2Context newInitiator(int account, long feePerB, long amount, String address, String paynymDestination) {
        return new Stonewallx2Context(CahootsTypeUser.SENDER, account, feePerB, amount, address, paynymDestination);
    }

    public static Stonewallx2Context newCounterparty(int account) {
        return new Stonewallx2Context(CahootsTypeUser.COUNTERPARTY, account, null,null, null, null);
    }

    public String getPaynymDestination() {
        return StringUtils.isEmpty(paynymDestination) ? null : paynymDestination;
    }
}
