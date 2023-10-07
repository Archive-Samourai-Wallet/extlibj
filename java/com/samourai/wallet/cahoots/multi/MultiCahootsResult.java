package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.api.backend.IPushTx;
import com.samourai.wallet.cahoots.CahootsResultImpl;
import com.samourai.wallet.util.TxUtil;

public class MultiCahootsResult extends CahootsResultImpl<MultiCahootsContext,MultiCahoots> {
    public MultiCahootsResult(MultiCahootsContext cahootsContext, MultiCahoots multiCahoots) {
        super(cahootsContext, multiCahoots,
                multiCahoots.getStonewallx2().getSpendAmount(),
                multiCahoots.getStonewallx2().getFeeAmount() + multiCahoots.getStowaway().getFeeAmount(),
                multiCahoots.getStonewallx2().getDestination(),
                multiCahoots.getStonewallx2().getPaynymDestination(),
                multiCahoots.getStonewallx2().getPSBT());
    }

    @Override
    public void pushTx(IPushTx pushTx) throws Exception {
        // push stonewallx2
        String stonewallHex = TxUtil.getInstance().getTxHex(getCahoots().getStonewallTransaction());
        pushTx.pushTx(stonewallHex);

        // push stowaway
        String stowawayHex = TxUtil.getInstance().getTxHex(getCahoots().getStowawayTransaction());
        pushTx.pushTx(stowawayHex);
    }
}
