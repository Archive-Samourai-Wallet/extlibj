package com.samourai.wallet.cahoots;

import com.samourai.wallet.soroban.client.SorobanMessage;

public class CahootsMessage implements SorobanMessage {
    private Cahoots cahoots;

    public static final int LAST_STEP = 4;
    public static final int NB_STEPS = LAST_STEP+1; // starting from 0

    public CahootsMessage(Cahoots cahoots) {
       this.cahoots = cahoots;
    }

    public static CahootsMessage parse(String payload) throws Exception {
        return new CahootsMessage(Cahoots.parse(payload));
    }

    public int getStep() {
        return cahoots.getStep();
    }

    public int getNbSteps() {
        return NB_STEPS;
    }

    @Override
    public boolean isLastMessage() {
        return getStep() == LAST_STEP;
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
        return "step="+getStep()+"/"+NB_STEPS+", type="+getType()+", typeUser="+getTypeUser()+", payload="+toPayload();
    }
}
