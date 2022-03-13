package com.samourai.wallet.api.backend;

import com.samourai.wallet.api.backend.beans.UnspentOutput;

import java.util.Collection;

public interface ISweepBackend {
    Collection<UnspentOutput> fetchAddressForSweep(String address) throws Exception;
    String pushTx(String txHex) throws Exception;
}
