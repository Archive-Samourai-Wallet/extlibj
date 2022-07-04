package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanMessage;
import com.samourai.wallet.cahoots.Cahoots;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsTypeUser;

public class ManualCahootsMessage implements SorobanMessage {
    private Cahoots cahoots;

    public static final int LAST_STEP = TypeInteraction.TX_BROADCAST.getStep();
    public static final int LAST_STEP_MULTI = TypeInteraction.TX_BROADCAST_MULTI.getStep();

    public ManualCahootsMessage(Cahoots cahoots) {
        this.cahoots = cahoots;
    }

    public static ManualCahootsMessage parse(String payload) throws Exception {
        return new ManualCahootsMessage(Cahoots.parse(payload));
    }

    public int getStep() {
        return cahoots.getStep();
    }

    protected int getLastStep() {
        return getType() == CahootsType.MULTI ? LAST_STEP_MULTI : LAST_STEP;
    }

    public int getNbSteps() {
        return getLastStep() + 1; // starting from 0
    }

    @Override
    public boolean isDone() {
        return getStep() == getLastStep();
    }

    public CahootsType getType() {
        return CahootsType.find(cahoots.getType()).get();
    }

    public CahootsTypeUser getTypeUser() {
        if (getStep()%2 == 0) {
            return CahootsTypeUser.SENDER;
        }
        return CahootsTypeUser.COUNTERPARTY;
    }

    @Override
    public String toPayload() {
        return cahoots.toJSONString();
    }

    public Cahoots getCahoots() {
        return cahoots;
    }

    @Override
    public String toString() {
        return "(ManualCahootsMessage)step="+getStep()+"/"+getNbSteps()+", type="+getType()+", typeUser="+getTypeUser()+", payload="+toPayload();
    }
}
