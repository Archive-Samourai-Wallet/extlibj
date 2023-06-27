package com.samourai.whirlpool.client.wallet.beans;

import com.samourai.dex.config.DexConfigProvider;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

@Deprecated // use WhirlpoolNetwork instead
public enum WhirlpoolServer {
  TESTNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerTestnetOnion(),
          WhirlpoolNetwork.TESTNET),
  INTEGRATION(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationOnion(),
          WhirlpoolNetwork.INTEGRATION),
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetOnion(),
          WhirlpoolNetwork.MAINNET),
  LOCAL_TESTNET("http://127.0.0.1:8080", "http://127.0.0.1:8080", WhirlpoolNetwork.LOCAL_TESTNET); // TODO

  private String serverUrlClear;
  private String serverUrlOnion;
  private WhirlpoolNetwork whirlpoolNetwork;

  WhirlpoolServer(
          String serverUrlClear,
          String serverUrlOnion,
          WhirlpoolNetwork whirlpoolNetwork) {
    this.serverUrlClear = serverUrlClear;
    this.serverUrlOnion = serverUrlOnion;
    this.whirlpoolNetwork = whirlpoolNetwork;
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

  public WhirlpoolNetwork getWhirlpoolNetwork() {
    return whirlpoolNetwork;
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
