package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import com.samourai.xmanager.client.XManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class CahootsContext implements SorobanContext {
    private static final Logger log = LoggerFactory.getLogger(CahootsContext.class);

    private CahootsWallet cahootsWallet;
    private CahootsTypeUser typeUser;
    private CahootsType cahootsType;
    private int account;
    private Long feePerB; // only set for initiator
    private Long amount; // only set for initiator
    private String address; // only set for initiator
    private Set<String> outputAddresses;

    protected CahootsContext(CahootsWallet cahootsWallet, CahootsTypeUser typeUser, CahootsType cahootsType, int account, Long feePerB, Long amount, String address) {
        this.cahootsWallet = cahootsWallet;
        this.typeUser = typeUser;
        this.cahootsType = cahootsType;
        this.account = account;
        this.feePerB = feePerB;
        this.amount = amount;
        this.address = address;
        this.outputAddresses = new LinkedHashSet<>();
    }

    public static CahootsContext newCounterparty(CahootsWallet cahootsWallet, CahootsType cahootsType, int account) {
        if (CahootsType.MULTI.equals(cahootsType)) {
            // MULTI counterparty is reserved to SAAS backend
            // via newCounterpartyMultiCahoots()
            throw new RuntimeException("MULTI counterparty is reserved to SAAS backend");
        }
        if (cahootsType.STOWAWAY.equals(cahootsType)) {
            // force account #0 for Stowaway counterparty (MULTI uses newCounterpartyStowawayMulti() to bypass it)
            account = 0;
        }
        return new CahootsContext(cahootsWallet, CahootsTypeUser.COUNTERPARTY, cahootsType, account, null,null, null);
    }

    public static CahootsContext newCounterpartyStowaway(CahootsWallet cahootsWallet, int account) {
        return newCounterparty(cahootsWallet, CahootsType.STOWAWAY, account);
    }

    public static CahootsContext newCounterpartyStowawayMulti(CahootsWallet cahootsWallet, int account) {
        // allow non-zero account for Stowaway MULTI
        return new CahootsContext(cahootsWallet, CahootsTypeUser.COUNTERPARTY, CahootsType.STOWAWAY, account, null,null, null);
    }

    public static CahootsContext newCounterpartyStonewallx2(CahootsWallet cahootsWallet, int account) {
        return newCounterparty(cahootsWallet, CahootsType.STONEWALLX2, account);
    }

    public static CahootsContext newCounterpartyMultiCahoots(CahootsWallet cahootsWallet, int account, XManagerClient xManagerClient) {
        return new MultiCahootsContext(cahootsWallet, CahootsTypeUser.COUNTERPARTY, account, null, null, null, xManagerClient);
    }

    public static CahootsContext newInitiatorStowaway(CahootsWallet cahootsWallet, int account, long feePerB, long amount) {
        return new CahootsContext(cahootsWallet, CahootsTypeUser.SENDER, CahootsType.STOWAWAY, account, feePerB, amount, null);
    }

    public static CahootsContext newInitiatorStonewallx2(CahootsWallet cahootsWallet, int account, long feePerB, long amount, String address) {
        return new CahootsContext(cahootsWallet, CahootsTypeUser.SENDER, CahootsType.STONEWALLX2, account, feePerB, amount, address);
    }

    public static CahootsContext newInitiatorMultiCahoots(CahootsWallet cahootsWallet, int account, long feePerB, long amount, String address) {
        return new MultiCahootsContext(cahootsWallet, CahootsTypeUser.SENDER, account, feePerB, amount, address, null);
    }

    public CahootsWallet getCahootsWallet() {
        return cahootsWallet;
    }

    public CahootsTypeUser getTypeUser() {
        return typeUser;
    }

    public CahootsType getCahootsType() {
        return cahootsType;
    }

    public int getAccount() {
        return account;
    }

    public Long getFeePerB() {
        return feePerB;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public Set<String> getOutputAddresses() {
        return outputAddresses;
    }

    public void addOutputAddress(String address) {
        outputAddresses.add(address);
    }
}
