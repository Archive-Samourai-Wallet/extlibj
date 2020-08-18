package com.samourai.wallet.cahoots;

public class CahootsMessage {
    private Cahoots cahoots;

    private static final int LAST_STEP = 4;

    public CahootsMessage(Cahoots cahoots) {
       this.cahoots = cahoots;
    }

    public int getStep() {
        return cahoots.getStep();
    }

    public boolean isLastStep() {
        return getStep() == LAST_STEP;
    }

    public int getType() {
        return cahoots.getType();
    }

    public String getPayload() {
        return cahoots.toJSONString();
    }

    public Cahoots getCahoots() {
        return cahoots;
    }
}
