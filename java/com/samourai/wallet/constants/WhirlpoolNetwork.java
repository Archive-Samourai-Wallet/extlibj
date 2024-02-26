package com.samourai.wallet.constants;

import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

public enum WhirlpoolNetwork {
  TESTNET(
          TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJSs3yAbowqwTXs3YmkJZ6JARF87uf35MztevhXtAsvv2hRHhSt4phK3PLJ6HmDiyzvdbYBNawkncG6fnH5mGqMmY6rB6DcMUfgcZME6g7soodeHR"), // TODO
  INTEGRATION(
          TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJSs3yAbowqwTXs3YmkJZ6JARF87uf35MztevhXtAsvv2hRHhSt4phK3PLJ6HmDiyzvdbYBNawkncG6fnH5mGqMmY6rB6DcMUfgcZME6g7soodeHR"), // TODO
  MAINNET(
          MainNetParams.get(),
          "1NwVafYT1s6SF5Atusv7A8MASzCvGruGXq",
          "PM8TJSs3yAbowqwTXs3YmkJZ6JARF87uf35MztevhXtAsvv2hRHhSt4phK3PLJ6HmDiyzvdbYBNawkncG6fnH5mGqMmY6rB6DcMUfgcZME6g7soodeHR"), // TODO
  LOCAL_TESTNET(TestNet3Params.get(),
          "mi42XN9J3eLdZae4tjQnJnVkCcNDRuAtz4",
          "PM8TJSs3yAbowqwTXs3YmkJZ6JARF87uf35MztevhXtAsvv2hRHhSt4phK3PLJ6HmDiyzvdbYBNawkncG6fnH5mGqMmY6rB6DcMUfgcZME6g7soodeHR"); // TODO

  private NetworkParameters params;
  private String signingAddress;
  private PaymentCode signingPaymentCode;

  WhirlpoolNetwork(
          NetworkParameters params,
          String signingAddress,
          String signingPaymentCode) {
    this.params = params;
    this.signingAddress = signingAddress;
    this.signingPaymentCode = new PaymentCode(signingPaymentCode);
  }

  public NetworkParameters getParams() {
    return params;
  }

  public String getSigningAddress() {
    return signingAddress;
  }

  // for tests
  public void _setSigningAddress(String signingAddress) {
    this.signingAddress = signingAddress;
  }

  public PaymentCode getSigningPaymentCode() {
    return signingPaymentCode;
  }

  public static Optional<WhirlpoolNetwork> find(String value) {
    try {
      return Optional.of(valueOf(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static WhirlpoolNetwork getByNetworkParameters(NetworkParameters params) {
    boolean isTestnet = FormatsUtilGeneric.getInstance().isTestNet(params);
    WhirlpoolNetwork whirlpoolServer = isTestnet ? WhirlpoolNetwork.TESTNET : WhirlpoolNetwork.MAINNET;
    return whirlpoolServer;
  }
}
