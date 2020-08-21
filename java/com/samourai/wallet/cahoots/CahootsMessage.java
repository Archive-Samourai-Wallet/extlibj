package com.samourai.wallet.cahoots;

public class CahootsMessage {
    private Cahoots cahoots;

    public static final int LAST_STEP = 4;
    public static final int NB_STEPS = LAST_STEP+1; // starting from 0

    public CahootsMessage(Cahoots cahoots) {
       this.cahoots = cahoots;
    }

    public int getStep() {
        return cahoots.getStep();
    }

    public boolean isLastStep() {
        return getStep() == LAST_STEP;
    }

    public CahootsType getType() {
        return CahootsType.find(cahoots.getType()).get();
    }

    public CahootsTypeUser getTypeUser() {
        if (getStep()%2 == 0) {
            return CahootsTypeUser.SENDER;
        }
        if (CahootsType.STONEWALLX2.equals(getType())) {
            return CahootsTypeUser.COUNTERPARTY;
        }
        return CahootsTypeUser.RECEIVER;
    }

    public String getPayload() {
        return cahoots.toJSONString();
    }

    public Cahoots getCahoots() {
        return cahoots;
    }

    @Override
    public String toString() {
        return "step="+getStep()+"/"+NB_STEPS+", type="+getType()+", typeUser="+getTypeUser()+", payload="+getPayload();
    }
}
