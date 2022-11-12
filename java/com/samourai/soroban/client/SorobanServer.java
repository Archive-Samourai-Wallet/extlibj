package com.samourai.soroban.client;

import com.samourai.dex.config.DexConfigProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

public enum SorobanServer {
  TESTNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerTestnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerTestnetOnion(),
          TestNet3Params.get()),
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerMainnetOnion(),
          MainNetParams.get());

  private String serverUrlClear;
  private String serverUrlOnion;
  private NetworkParameters params;

  SorobanServer(String serverUrlClear, String serverUrlOnion, NetworkParameters params) {
    this.serverUrlClear = serverUrlClear;
    this.serverUrlOnion = serverUrlOnion;
    this.params = params;
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

  public static Optional<SorobanServer> find(String value) {
    try {
      return Optional.of(valueOf(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static SorobanServer get(NetworkParameters params) {
    if (FormatsUtilGeneric.getInstance().isTestNet(params)) {
      return TESTNET;
    }
    return MAINNET;
  }
}
