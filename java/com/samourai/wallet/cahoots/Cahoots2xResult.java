package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.api.backend.IPushTx;
import com.samourai.wallet.util.TxUtil;

public class Cahoots2xResult extends CahootsResultImpl<CahootsContext,Cahoots2x> {

    public Cahoots2xResult(CahootsContext cahootsContext, Cahoots2x cahoots) {
        super(cahootsContext, cahoots,
                cahoots.getSpendAmount(),
                cahoots.getFeeAmount(),
                cahoots.getDestination(),
                cahoots.getPaynymDestination(),
                cahoots.getPSBT());
    }

    @Override
    public void pushTx(IPushTx pushTx) throws Exception {
        String txHex = TxUtil.getInstance().getTxHex(getCahoots().getTransaction());
        pushTx.pushTx(txHex);
    }
}
