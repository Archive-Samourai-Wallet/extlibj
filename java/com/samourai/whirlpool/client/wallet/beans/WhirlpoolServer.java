package com.samourai.whirlpool.client.wallet.beans;

import com.samourai.dex.config.DexConfigProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

public enum WhirlpoolServer {
  TESTNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetOnion(),
          TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4"),
  INTEGRATION(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationOnion(),
          TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4"),
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetOnion(),
          MainNetParams.get(),
          "1NwVafYT1s6SF5Atusv7A8MASzCvGruGXq"),
  LOCAL_TESTNET("http://127.0.0.1:8080", "http://127.0.0.1:8080", TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4");

  private String serverUrlClear;
  private String serverUrlOnion;
  private NetworkParameters params;
  private String signingAddress;

  WhirlpoolServer(
          String serverUrlClear,
          String serverUrlOnion,
          NetworkParameters params,
          String signingAddress) {
    this.serverUrlClear = serverUrlClear;
    this.serverUrlOnion = serverUrlOnion;
    this.params = params;
    this.signingAddress = signingAddress;
  }

  public String getServerUrlClear() {
    return serverUrlClear;
  }

  public String getServerUrlOnion() {
    return serverUrlOnion;
  }

  public String getServerUrl(boolean onion) {
    String serverUrl = onion ? getServerUrlOnion() : getServerUrlClear();
    return serverUrl;
  }

  public NetworkParameters getParams() {
    return params;
  }

  public String getSigningAddress() {
    return signingAddress;
  }

  public static Optional<WhirlpoolServer> find(String value) {
    try {
      return Optional.of(valueOf(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static WhirlpoolServer getByNetworkParameters(NetworkParameters params) {
    boolean isTestnet = FormatsUtilGeneric.getInstance().isTestNet(params);
    WhirlpoolServer whirlpoolServer = isTestnet ? WhirlpoolServer.TESTNET : WhirlpoolServer.MAINNET;
    return whirlpoolServer;
  }
}
