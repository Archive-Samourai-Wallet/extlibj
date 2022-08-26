package com.samourai.dex.config;

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
        this.load();
    }

    protected void load() {
        // TODO load remote config here, fallback to default if any issue
    }

    public SamouraiConfig getSamouraiConfig() {
        return samouraiConfig;
    }
}
