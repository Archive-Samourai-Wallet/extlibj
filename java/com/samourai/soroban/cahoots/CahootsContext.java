package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanContext;
import com.samourai.wallet.cahoots.Cahoots;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
    private List<CahootsUtxo> inputs;
    private Boolean rbfOptin;

    protected CahootsContext(CahootsTypeUser typeUser, CahootsType cahootsType, int account, Long feePerB, Long amount, String address, Boolean rbfOptin) {
        this.typeUser = typeUser;
        this.cahootsType = cahootsType;
        this.account = account;
        this.feePerB = feePerB;
        this.amount = amount;
        this.address = address;
        this.outputAddresses = new LinkedHashSet<>();
        this.inputs = new LinkedList<>();
        this.rbfOptin = rbfOptin;
    }

    public static CahootsContext newCounterparty(CahootsType cahootsType, int account) throws Exception {
        CahootsContext cahootsContext = null;
        switch (cahootsType) {
            case MULTI:
                cahootsContext = MultiCahootsContext.newCounterparty(account);
                break;

            case STONEWALLX2:
                cahootsContext = Stonewallx2Context.newCounterparty(account);
                break;

            case STOWAWAY:
                cahootsContext = StowawayContext.newCounterparty(account);
                break;

            default:
                throw new Exception("Unknown Cahoots type");
        }
        return cahootsContext;
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

    public void addInput(CahootsUtxo cahootsUtxo) {
        inputs.add(cahootsUtxo);
    }

    public List<CahootsUtxo> getInputs() {
        return inputs;
    }

    public boolean isRbfOptin() {
        if (rbfOptin == null)
            return false;
        return rbfOptin;
    }

    public void setRbfOptin(Boolean rbfOptin) {
        this.rbfOptin = rbfOptin;
    }
}
