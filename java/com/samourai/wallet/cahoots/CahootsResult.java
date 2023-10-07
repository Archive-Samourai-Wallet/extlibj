package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.api.backend.IPushTx;
import com.samourai.wallet.cahoots.psbt.PSBT;

// object resulting of a successfully Cahoots, used by Android ReviewFragment
public interface CahootsResult<C extends CahootsContext, T extends Cahoots<C>> {
    C getCahootsContext();
    T getCahoots();

    long getSpendAmount();
    long getFeeAmount();
    String getDestination();

    // android checks getPaynymDestination() to increment paynym counter after successfull broadcast
    String getPaynymDestination();

    // PSBT to display on Android
    PSBT getPsbt();

    void pushTx(IPushTx pushTx) throws Exception;
}
