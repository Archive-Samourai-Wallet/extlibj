package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.soroban.client.SorobanMessage;
import com.samourai.wallet.cahoots.CahootsResult;

public class TxBroadcastInteraction extends SorobanInteraction {
    private CahootsResult cahootsResult;

    public TxBroadcastInteraction(TypeInteraction typeInteraction, SorobanMessage replyAccept, CahootsContext cahootsContext, CahootsResult cahootsResult) {
        super(typeInteraction, replyAccept, cahootsContext);
        this.cahootsResult = cahootsResult;
    }

    public CahootsResult getCahootsResult() {
        return cahootsResult;
    }
}
