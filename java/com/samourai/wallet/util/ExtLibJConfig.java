package com.samourai.wallet.util;

import com.samourai.wallet.bip47.BIP47UtilGeneric;
import com.samourai.wallet.bip47.rpc.java.Bip47UtilJava;
import com.samourai.wallet.bip47.rpc.java.SecretPointFactoryJava;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPointFactory;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.constants.WhirlpoolNetwork;
import com.samourai.wallet.crypto.CryptoUtil;
import com.samourai.wallet.httpClient.IHttpClientService;

import java.security.Provider;

public class ExtLibJConfig {
    protected WhirlpoolNetwork whirlpoolNetwork;
    protected boolean onion;
    protected IHttpClientService httpClientService;
    protected CryptoUtil cryptoUtil;
    protected BIP47UtilGeneric bip47Util;
    protected BipFormatSupplier bipFormatSupplier;
    private ISecretPointFactory secretPointFactory;


    public ExtLibJConfig(WhirlpoolNetwork whirlpoolNetwork, boolean onion, Provider provider, IHttpClientService httpClientService) {
        this(whirlpoolNetwork,
                 onion,
                 httpClientService,
                CryptoUtil.getInstance(provider),
                Bip47UtilJava.getInstance(),
                BIP_FORMAT.PROVIDER,
                SecretPointFactoryJava.getInstance());
    }
    
    public ExtLibJConfig(WhirlpoolNetwork whirlpoolNetwork, boolean onion, IHttpClientService httpClientService, CryptoUtil cryptoUtil, BIP47UtilGeneric bip47Util, BipFormatSupplier bipFormatSupplier, ISecretPointFactory secretPointFactory) {
        this.whirlpoolNetwork = whirlpoolNetwork;
        this.onion = onion;
        this.httpClientService = httpClientService;
        this.cryptoUtil = cryptoUtil;
        this.bip47Util = bip47Util;
        this.bipFormatSupplier = bipFormatSupplier;
        this.secretPointFactory = secretPointFactory;
    }

    public WhirlpoolNetwork getWhirlpoolNetwork() {
        return whirlpoolNetwork;
    }

    public boolean isOnion() {
        return onion;
    }

    public IHttpClientService getHttpClientService() {
        return httpClientService;
    }

    public CryptoUtil getCryptoUtil() {
        return cryptoUtil;
    }

    public BIP47UtilGeneric getBip47Util() {
        return bip47Util;
    }

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    public ISecretPointFactory getSecretPointFactory() {
        return secretPointFactory;
    }
}