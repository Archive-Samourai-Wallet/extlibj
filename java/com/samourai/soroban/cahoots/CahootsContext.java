package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanContext;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CahootsContext implements SorobanContext {
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
