package com.samourai.wallet.cahoots.multi;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.Stonewallx2Context;
import com.samourai.soroban.cahoots.StowawayContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.stowaway.Stowaway;

public class MultiCahootsContext extends CahootsContext {
    private Stonewallx2Context stonewallx2Context;
    private StowawayContext stowawayContext;

    protected MultiCahootsContext(CahootsTypeUser typeUser, int account, Long feePerB, Long amount, String address) {
        super(typeUser, CahootsType.MULTI, account, feePerB, amount, address);
        this.stonewallx2Context = computeStonewallContext();
        this.stowawayContext = computeStowawayContext();
    }

    public static MultiCahootsContext newInitiator(int account, long feePerB, long amount, String address) {
        return new MultiCahootsContext(CahootsTypeUser.SENDER, account, feePerB, amount, address);
    }

    public static MultiCahootsContext newCounterparty(int account) {
        return new MultiCahootsContext(CahootsTypeUser.COUNTERPARTY, account, null,null, null);
    }

    private Stonewallx2Context computeStonewallContext() {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return Stonewallx2Context.newCounterparty(getAccount());
        }
        return Stonewallx2Context.newInitiator(getAccount(), getFeePerB(), getAmount(), getAddress());
    }

    private StowawayContext computeStowawayContext() {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return StowawayContext.newCounterpartyMulti(getAccount());
        }
        return StowawayContext.newInitiator(getAccount(), getFeePerB(), -1L);
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
