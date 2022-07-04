package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.cahoots.Cahoots;

public class TxBroadcastInteraction extends SorobanInteraction {
    private Cahoots signedCahoots;

    public TxBroadcastInteraction(Cahoots signedCahoots) {
        this(TypeInteraction.TX_BROADCAST, signedCahoots);
    }

    public TxBroadcastInteraction(TypeInteraction typeInteraction, Cahoots signedCahoots) {
        super(typeInteraction, new ManualCahootsMessage(signedCahoots));
        this.signedCahoots = signedCahoots;
    }

    public Cahoots getSignedCahoots() {
        return signedCahoots;
    }
}
