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
        // config will be loaded from remote server here
    }

    public SamouraiConfig getSamouraiConfig() {
        return samouraiConfig;
    }
}
