package com.samourai.soroban.client;

import com.samourai.wallet.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSorobanMessageSimple implements SorobanMessageSimple {
    private static final Logger log = LoggerFactory.getLogger(AbstractSorobanMessageSimple.class);

    @Override
    public String toPayload() {
        try {
            return JSONUtils.getInstance().getObjectMapper().writeValueAsString(this);
        } catch(Exception e) {
            log.error("", e);
            return null;
        }
    }
}
