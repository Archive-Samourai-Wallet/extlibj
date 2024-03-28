package com.samourai.wallet.constants;

import com.samourai.wallet.dexConfig.DexConfigProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;

import java.util.Optional;

@Deprecated // use SamouraiNetwork instead
public enum WhirlpoolServer {
  TESTNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetOnion(),
          SamouraiNetwork.TESTNET),
  INTEGRATION(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationOnion(),
          SamouraiNetwork.INTEGRATION),
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetOnion(),
          SamouraiNetwork.MAINNET),
  LOCAL_TESTNET("http://127.0.0.1:8080", "http://127.0.0.1:8080", SamouraiNetwork.LOCAL_TESTNET); // TODO

  private String serverUrlClear;
  private String serverUrlOnion;
  private SamouraiNetwork samouraiNetwork;

  WhirlpoolServer(
          String serverUrlClear,
          String serverUrlOnion,
          SamouraiNetwork samouraiNetwork) {
    this.serverUrlClear = serverUrlClear;
    this.serverUrlOnion = serverUrlOnion;
    this.samouraiNetwork = samouraiNetwork;
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

  public SamouraiNetwork getSamouraiNetwork() {
    return samouraiNetwork;
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
