package com.samourai.dex.config;

import com.samourai.wallet.util.JSONUtils;

public class SamouraiConfig {
    // extlibj: BackendServer
    private String backendServerMainnetClear = "https://api.samouraiwallet.com/v2";
    private String backendServerMainnetOnion = "http://d2oagweysnavqgcfsfawqwql2rwxend7xxpriq676lzsmtfwbt75qbqd.onion/v2";
    private String backendServerTestnetClear = "https://api.samouraiwallet.com/test/v2";
    private String backendServerTestnetOnion = "http://d2oagweysnavqgcfsfawqwql2rwxend7xxpriq676lzsmtfwbt75qbqd.onion/test/v2";

    // extlibj: SorobanServer
    private String sorobanServerTestnetClear = "https://soroban.samouraiwallet.com/test";
    private String sorobanServerTestnetOnion = "http://sorob4sg7yiopktgz4eom7hl5mcodr6quvhmdpljl5qqhmt6po7oebid.onion/test";
    private String sorobanServerMainnetClear = "https://soroban.samouraiwallet.com";
    private String getSorobanServerMainnetOnion = "http://sorob4sg7yiopktgz4eom7hl5mcodr6quvhmdpljl5qqhmt6po7oebid.onion";


    public SamouraiConfig() {
    }

    public String getBackendServerMainnetClear() {
        return backendServerMainnetClear;
    }

    public void setBackendServerMainnetClear(String backendServerMainnetClear) {
        this.backendServerMainnetClear = backendServerMainnetClear;
    }

    public String getBackendServerMainnetOnion() {
        return backendServerMainnetOnion;
    }

    public void setBackendServerMainnetOnion(String backendServerMainnetOnion) {
        this.backendServerMainnetOnion = backendServerMainnetOnion;
    }

    public String getBackendServerTestnetClear() {
        return backendServerTestnetClear;
    }

    public void setBackendServerTestnetClear(String backendServerTestnetClear) {
        this.backendServerTestnetClear = backendServerTestnetClear;
    }

    public String getBackendServerTestnetOnion() {
        return backendServerTestnetOnion;
    }

    public void setBackendServerTestnetOnion(String backendServerTestnetOnion) {
        this.backendServerTestnetOnion = backendServerTestnetOnion;
    }
    public String getSorobanServerTestnetClear() {
        return sorobanServerTestnetClear;
    }

    public void setSorobanServerTestnetClear(String sorobanServerTestnetClear) {
        this.sorobanServerTestnetClear = sorobanServerTestnetClear;
    }

    public String getSorobanServerTestnetOnion() {
        return sorobanServerTestnetOnion;
    }

    public void setSorobanServerTestnetOnion(String sorobanServerTestnetOnion) {
        this.sorobanServerTestnetOnion = sorobanServerTestnetOnion;
    }

    public String getSorobanServerMainnetClear() {
        return sorobanServerMainnetClear;
    }

    public void setSorobanServerMainnetClear(String sorobanServerMainnetClear) {
        this.sorobanServerMainnetClear = sorobanServerMainnetClear;
    }

    public String getGetSorobanServerMainnetOnion() {
        return getSorobanServerMainnetOnion;
    }

    public void setGetSorobanServerMainnetOnion(String getSorobanServerMainnetOnion) {
        this.getSorobanServerMainnetOnion = getSorobanServerMainnetOnion;
    }
}
