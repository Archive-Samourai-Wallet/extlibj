package com.samourai.wallet.cahoots.multi;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.Stonewallx2Context;
import com.samourai.soroban.cahoots.StowawayContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;

public class MultiCahootsContext extends CahootsContext {
    private Stonewallx2Context stonewallx2Context;
    private StowawayContext stowawayContext;

    protected MultiCahootsContext(CahootsTypeUser typeUser, int account, Long feePerB, Long amount, String address, String paynymDestination, Boolean rbfOptin) {
        super(typeUser, CahootsType.MULTI, account, feePerB, amount, address, rbfOptin);
        this.stonewallx2Context = computeStonewallContext(paynymDestination);
        this.stowawayContext = computeStowawayContext();
    }

    public static MultiCahootsContext newInitiator(int account, long feePerB, long amount, String address, String paynymDestination, Boolean rbfOptin) {
        return new MultiCahootsContext(CahootsTypeUser.SENDER, account, feePerB, amount, address, paynymDestination, rbfOptin);
    }

    public static MultiCahootsContext newCounterparty(int account) {
        return new MultiCahootsContext(CahootsTypeUser.COUNTERPARTY, account, null,null, null, null, null);
    }

    private Stonewallx2Context computeStonewallContext(String paynymDestination) {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return Stonewallx2Context.newCounterparty(getAccount());
        }
        return Stonewallx2Context.newInitiator(getAccount(), getFeePerB(), getAmount(), getAddress(), paynymDestination, isRbfOptin());
    }

    private StowawayContext computeStowawayContext() {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return StowawayContext.newCounterpartyMulti(getAccount());
        }
        return StowawayContext.newInitiator(getAccount(), getFeePerB(), -1L, isRbfOptin());
    }

    public static long computeMultiCahootsFee(long amount) {
        long stowawayFee = (long)(amount * 0.035d);
        if(stowawayFee > 1000000) {
            stowawayFee = 1000000;
        }
        return stowawayFee;
    }

    public Stonewallx2Context getStonewallx2Context() {
        return stonewallx2Context;
    }

    public StowawayContext getStowawayContext() {
        return stowawayContext;
    }
}
