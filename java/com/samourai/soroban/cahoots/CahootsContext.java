package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanContext;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.UtxoOutPoint;
import org.bitcoinj.core.TransactionOutPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class CahootsContext implements SorobanContext {
    private static final Logger log = LoggerFactory.getLogger(CahootsContext.class);
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();

    private CahootsWallet cahootsWallet;
    private CahootsTypeUser typeUser;
    private CahootsType cahootsType;
    private int account;
    private Long feePerB; // only set for initiator
    private Long amount; // only set for initiator
    private String address; // only set for initiator
    private Set<String> outputAddresses; // keep track of our own change addresses outputs
    private Collection<UtxoOutPoint> inputs; // keep track of our own inputs
    private long samouraiFee; // keep track of samourai fee
    private long minerFeePaid; // keep track of paid minerFee (lower or equals cahoots.fee)
    private KeyBag keyBag;

    protected CahootsContext(CahootsWallet cahootsWallet, CahootsTypeUser typeUser, CahootsType cahootsType, int account, Long feePerB, Long amount, String address) {
        this.cahootsWallet = cahootsWallet;
        this.typeUser = typeUser;
        this.cahootsType = cahootsType;
        this.account = account;
        this.feePerB = feePerB;
        this.amount = amount;
        this.address = address;
        this.outputAddresses = new LinkedHashSet<>();
        this.inputs = new LinkedList<>();
        this.samouraiFee = 0;
        this.minerFeePaid = 0;
        this.keyBag = new KeyBag();
    }

    public static CahootsContext newCounterparty(CahootsWallet cahootsWallet, CahootsType cahootsType, int account) throws Exception {
        CahootsContext cahootsContext = null;
        switch (cahootsType) {
            case MULTI:
                // MULTI counterparty is reserved to SAAS backend via newCounterpartyMultiCahoots()
                throw new RuntimeException("MULTI counterparty is reserved to SAAS backend");

            case STONEWALLX2:
                cahootsContext = Stonewallx2Context.newCounterparty(cahootsWallet, account);
                break;

            case STOWAWAY:
                cahootsContext = StowawayContext.newCounterparty(cahootsWallet, account);
                break;

            default:
                throw new Exception("Unknown Cahoots type");
        }
        return cahootsContext;
    }

    public static CahootsContext newInitiator(CahootsWallet cahootsWallet, CahootsType cahootsType, int account, long feePerB, long amount, String address, String paynymDestination) throws Exception {
        switch (cahootsType) {
            case STONEWALLX2:
                return Stonewallx2Context.newInitiator(
                                cahootsWallet, account, feePerB, amount, address, paynymDestination);

            case STOWAWAY:
                return StowawayContext.newInitiator(cahootsWallet, account, feePerB, amount);

            case MULTI:
                return MultiCahootsContext.newInitiator(cahootsWallet, account, feePerB, amount, address, paynymDestination);

            default:
                throw new Exception("Unknown CahootsType");
        }
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

    public void addInput(UtxoOutPoint input, byte[] key) {
        inputs.add(input);
        keyBag.add(input, key);
    }

    public void addInputs(Collection<CahootsUtxo> inputs) {
        for (CahootsUtxo input : inputs) {
            addInput(input, input.getKey());
        }
    }

    public void addInputs(Collection<UtxoOutPoint> inputs, KeyBag keyBag) {
        for (UtxoOutPoint input : inputs) {
            byte[] key = keyBag.getPrivKeyBytes(input);
            addInput(input, key);
        }
    }

    public UtxoOutPoint findInput(TransactionOutPoint inputOutPoint) {
        String utxoKey = utxoUtil.utxoToKey(inputOutPoint);
        return inputs.stream().filter(in ->
                        utxoUtil.utxoToKey(in).equals(utxoKey))
                        .findFirst().orElse(null);
    }

    public Collection<UtxoOutPoint> getInputs() {
        return inputs;
    }

    public long getSamouraiFee() {
        return samouraiFee;
    }

    public void setSamouraiFee(long samouraiFee) {
        this.samouraiFee = samouraiFee;
    }

    public long getMinerFeePaid() {
        return minerFeePaid;
    }

    public void setMinerFeePaid(long minerFeePaid) {
        this.minerFeePaid = minerFeePaid;
    }

    public KeyBag getKeyBag() {
        return keyBag;
    }
}
