package com.samourai.wallet.cahoots;

import org.bitcoinj.core.NetworkParameters;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCahootsService<T extends Cahoots> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsService.class);

    protected NetworkParameters params;

    public AbstractCahootsService(NetworkParameters params) {
        this.params = params;
    }

    public abstract T startCollaborator(T payload0, CahootsWallet cahootsWallet, int account) throws Exception;

    public abstract T reply(T payload, CahootsWallet cahootsWallet, long feePerB) throws Exception;
}
