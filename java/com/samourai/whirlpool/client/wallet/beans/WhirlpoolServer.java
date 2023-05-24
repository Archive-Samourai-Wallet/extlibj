package com.samourai.whirlpool.client.wallet.beans;

import com.samourai.dex.config.DexConfigProvider;
import com.samourai.wallet.bip47.rpc.PaymentCode;
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
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJPTDuJei5ML1FinhAKuwHHfdBYq66zTdcqM2RMqAywefDeH6h5YAFP2hZDAdSkx2kK1s2XFnixQBPYw2YWDMdz4vbcePFGfNNVGLkyKFAYKcrqvZ"), // TODO
  INTEGRATION(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerIntegrationOnion(),
          TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJPTDuJei5ML1FinhAKuwHHfdBYq66zTdcqM2RMqAywefDeH6h5YAFP2hZDAdSkx2kK1s2XFnixQBPYw2YWDMdz4vbcePFGfNNVGLkyKFAYKcrqvZ"), // TODO
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getWhirlpoolServerMainnetOnion(),
          MainNetParams.get(),
          "1NwVafYT1s6SF5Atusv7A8MASzCvGruGXq",
          "PM8TJPTDuJei5ML1FinhAKuwHHfdBYq66zTdcqM2RMqAywefDeH6h5YAFP2hZDAdSkx2kK1s2XFnixQBPYw2YWDMdz4vbcePFGfNNVGLkyKFAYKcrqvZ"), // TODO
  LOCAL_TESTNET("http://127.0.0.1:8080", "http://127.0.0.1:8080", TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJPTDuJei5ML1FinhAKuwHHfdBYq66zTdcqM2RMqAywefDeH6h5YAFP2hZDAdSkx2kK1s2XFnixQBPYw2YWDMdz4vbcePFGfNNVGLkyKFAYKcrqvZ"); // TODO

  private String serverUrlClear;
  private String serverUrlOnion;
  private NetworkParameters params;
  private String signingAddress;
  private PaymentCode signingPaymentCode;

  WhirlpoolServer(
          String serverUrlClear,
          String serverUrlOnion,
          NetworkParameters params,
          String signingAddress,
          String signingPaymentCode) {
    this.serverUrlClear = serverUrlClear;
    this.serverUrlOnion = serverUrlOnion;
    this.params = params;
    this.signingAddress = signingAddress;
    this.signingPaymentCode = new PaymentCode(signingPaymentCode);
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

  public PaymentCode getSigningPaymentCode() {
    return signingPaymentCode;
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
