package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class CahootsContext implements SorobanContext {
    private static final Logger log = LoggerFactory.getLogger(CahootsContext.class);

    private CahootsTypeUser typeUser;
    private CahootsType cahootsType;
    private int account;
    private Long feePerB; // only set for initiator
    private Long amount; // only set for initiator
    private String address; // only set for initiator
    private Set<String> outputAddresses;

    protected CahootsContext(CahootsTypeUser typeUser, CahootsType cahootsType, int account, Long feePerB, Long amount, String address) {
        this.typeUser = typeUser;
        this.cahootsType = cahootsType;
        this.account = account;
        this.feePerB = feePerB;
        this.amount = amount;
        this.address = address;
        this.outputAddresses = new LinkedHashSet<>();
    }

    public static CahootsContext newCounterparty(CahootsType cahootsType, int account) {
        if (CahootsType.MULTI.equals(cahootsType)) {
            return new MultiCahootsContext(CahootsTypeUser.COUNTERPARTY, account, null,null, null);
        }
        if (cahootsType.STOWAWAY.equals(cahootsType)) {
            // force account #0 for Stowaway counterparty
            account = 0;
        }
        return new CahootsContext(CahootsTypeUser.COUNTERPARTY, cahootsType, account, null,null, null);
    }

    public static CahootsContext newCounterpartyStowaway(int account) {
        return newCounterparty(CahootsType.STOWAWAY, account);
    }

    public static CahootsContext newCounterpartyStowawayMulti(int account) {
        // allow non-zero account for Stowaway MULTI
        return new CahootsContext(CahootsTypeUser.COUNTERPARTY, CahootsType.STOWAWAY, account, null,null, null);
    }

    public static CahootsContext newCounterpartyStonewallx2(int account) {
        return newCounterparty(CahootsType.STONEWALLX2, account);
    }

    public static CahootsContext newCounterpartyMultiCahoots(int account) {
        return newCounterparty(CahootsType.MULTI, account);
    }

    public static CahootsContext newInitiatorStowaway(int account, long feePerB, long amount) {
        return new CahootsContext(CahootsTypeUser.SENDER, CahootsType.STOWAWAY, account, feePerB, amount, null);
    }

    public static CahootsContext newInitiatorStonewallx2(int account, long feePerB, long amount, String address) {
        return new CahootsContext(CahootsTypeUser.SENDER, CahootsType.STONEWALLX2, account, feePerB, amount, address);
    }

    public static CahootsContext newInitiatorMultiCahoots(int account, long feePerB, long amount, String address) {
        return new MultiCahootsContext(CahootsTypeUser.SENDER, account, feePerB, amount, address);
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
