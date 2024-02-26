package com.samourai.wallet.dexConfig;

import com.samourai.soroban.client.SorobanServerDex;
import com.samourai.wallet.api.backend.IBackendClient;
import com.samourai.wallet.util.JSONUtils;
import com.samourai.wallet.util.MessageSignUtilGeneric;
import com.samourai.wallet.constants.WhirlpoolServer;
import org.bitcoinj.core.NetworkParameters;

public class DexConfigProvider {
    public static final String ENDPOINT_DEXCONFIG = "/rest/dex-config";
    private static DexConfigProvider instance;

    public static DexConfigProvider getInstance() {
        if (instance == null) {
            instance = new DexConfigProvider();
        }
        return instance;
    }

    private SamouraiConfig samouraiConfig;
    private Long lastLoad = null;

    protected DexConfigProvider() {
        // initialize default config
        this.samouraiConfig = new SamouraiConfig();
    }

    public void load(IBackendClient httpClient, NetworkParameters networkParameters, boolean onion) throws Exception {
        WhirlpoolServer whirlpoolServer = WhirlpoolServer.getByNetworkParameters(networkParameters);
        String dexURL = whirlpoolServer.getServerUrl(onion) + ENDPOINT_DEXCONFIG;
        load(httpClient, networkParameters, dexURL, whirlpoolServer.getWhirlpoolNetwork().getSigningAddress());
    }

    public void load(IBackendClient httpClient, NetworkParameters networkParameters, String dexURL, String signingAddress) throws Exception {
        DexConfigResponse dexConfigResponse = httpClient.getJson(dexURL, DexConfigResponse.class, null);

        if (MessageSignUtilGeneric.getInstance().verifySignedMessage(
                signingAddress,
                dexConfigResponse.getSamouraiConfig(),
                dexConfigResponse.getSignature(),
                networkParameters)) {
            this.samouraiConfig = JSONUtils.getInstance().getObjectMapper().readValue(dexConfigResponse.getSamouraiConfig(), SamouraiConfig.class);
            this.lastLoad = System.currentTimeMillis();

            // update SorobanServerDex
            //SorobanServerDex.setFrom(samouraiConfig);
        } else {
            throw new Exception("Invalid DexConfig signature");
        }
    }

    public SamouraiConfig getSamouraiConfig() {
        return samouraiConfig;
    }

    public Long getLastLoad() {
        return lastLoad;
    }
}
