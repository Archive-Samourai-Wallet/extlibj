package com.samourai.soroban.client;

import com.samourai.dex.config.DexConfigProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.RandomUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

public enum SorobanServerDex {
  TESTNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerDexTestnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerDexTestnetOnion(),
          TestNet3Params.get()),
  MAINNET(
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerDexMainnetClear(),
          DexConfigProvider.getInstance().getSamouraiConfig().getSorobanServerDexMainnetOnion(),
          MainNetParams.get());

  private static final Logger log = LoggerFactory.getLogger(SorobanServerDex.class);
  private Collection<String> serverUrlsClear;
  private Collection<String> serverUrlsOnion;
  private NetworkParameters params;

  SorobanServerDex(Collection<String> serverUrlsClear, Collection<String> serverUrlsOnion, NetworkParameters params) {
    this.serverUrlsClear = serverUrlsClear;
    this.serverUrlsOnion = serverUrlsOnion;
    this.params = params;
  }

  public Collection<String> getServerUrlsClear() {
    return serverUrlsClear;
  }

  public Collection<String> getServerUrlsOnion() {
    return serverUrlsOnion;
  }

  public String getServerUrlRandom(boolean onion) {
    String url = RandomUtil.getInstance().next(getServerUrls(onion));
    if (log.isDebugEnabled()) {
      log.debug("using SorobanServer: "+url);
    }
    return url;
  }

  public Collection<String> getServerUrls(boolean onion) {
    Collection<String> serverUrls = onion ? getServerUrlsOnion() : getServerUrlsClear();
    return serverUrls;
  }

  public NetworkParameters getParams() {
    return params;
  }

  public static Optional<SorobanServerDex> find(String value) {
    try {
      return Optional.of(valueOf(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static SorobanServerDex get(NetworkParameters params) {
    if (FormatsUtilGeneric.getInstance().isTestNet(params)) {
      return TESTNET;
    }
    return MAINNET;
  }
}
