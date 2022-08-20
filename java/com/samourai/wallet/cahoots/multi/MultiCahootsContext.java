package com.samourai.wallet.cahoots.multi;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.xmanager.client.XManagerClient;

public class MultiCahootsContext extends CahootsContext {
    private CahootsContext stonewallx2Context;
    private CahootsContext stowawayContext;
    private XManagerClient xManagerClient; // only needed for counterparty

    public MultiCahootsContext(CahootsWallet cahootsWallet, CahootsTypeUser typeUser, int account, Long feePerB, Long amount, String address, XManagerClient xManagerClient) {
        super(cahootsWallet, typeUser, CahootsType.MULTI, account, feePerB, amount, address);
        this.stonewallx2Context = computeStonewallContext();
        this.stowawayContext = computeStowawayContext();
        this.xManagerClient = xManagerClient;
    }

    private CahootsContext computeStonewallContext() {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return CahootsContext.newCounterpartyStonewallx2(getCahootsWallet(), getAccount());
        }
        return CahootsContext.newInitiatorStonewallx2(getCahootsWallet(), getAccount(), getFeePerB(), getAmount(), getAddress());
    }

    private CahootsContext computeStowawayContext() {
        if (getTypeUser().equals(CahootsTypeUser.COUNTERPARTY)) {
            return CahootsContext.newCounterpartyStowawayMulti(getCahootsWallet(), getAccount());
        }
        return CahootsContext.newInitiatorStowaway(getCahootsWallet(), getAccount(), getFeePerB(), -1L);
    }

    public static long computeMultiCahootsFee(long amount) {
        long stowawayFee = (long)(amount * 0.035d);
        if(stowawayFee > 1000000) {
            stowawayFee = 1000000;
        }
        return stowawayFee;
    }

    public CahootsContext getStonewallx2Context() {
        return stonewallx2Context;
    }

    public CahootsContext getStowawayContext() {
        return stowawayContext;
    }

    public XManagerClient getxManagerClient() {
        return xManagerClient;
    }
}
