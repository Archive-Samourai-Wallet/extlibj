package com.samourai.dex.config;

import com.samourai.wallet.api.backend.IBackendClient;
import com.samourai.wallet.util.JSONUtils;
import com.samourai.wallet.util.MessageSignUtilGeneric;
import org.bitcoinj.core.NetworkParameters;

public class DexConfigProvider {
    private static DexConfigProvider instance;

    public static DexConfigProvider getInstance() {
        if (instance == null) {
            instance = new DexConfigProvider();
        }
        return instance;
    }

    private SamouraiConfig samouraiConfig;

    protected DexConfigProvider() {
        // initialize default config
        this.samouraiConfig = new SamouraiConfig();
    }

    public void load(IBackendClient httpClient, NetworkParameters networkParameters) {
        // TODO load remote config here, fallback to default if any issue
        String dexURL = "https://pool.whirl.mx:8081/rest/dex-config";
        try {
            DexConfigResponse dexConfigResponse = httpClient.getJson(dexURL, DexConfigResponse.class, null);

            if (MessageSignUtilGeneric.getInstance().verifySignedMessage(
                    DexConfigResponse.SIGNING_ADDRESS,
                    dexConfigResponse.getSamouraiConfig(),
                    dexConfigResponse.getSignature(),
                    networkParameters)) {
                this.samouraiConfig = JSONUtils.getInstance().getObjectMapper().readValue(dexConfigResponse.getSamouraiConfig(), SamouraiConfig.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SamouraiConfig getSamouraiConfig() {
        return samouraiConfig;
    }
}
