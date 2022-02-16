package com.samourai.wallet.test;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AbstractTest {
  protected static final Logger log = LoggerFactory.getLogger(AbstractTest.class);

  protected static final String SEED_WORDS = "all all all all all all all all all all all all";
  protected static final String SEED_PASSPHRASE = "whirlpool";

  protected NetworkParameters params = TestNet3Params.get();
  protected HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();
  protected IHttpClient httpClient;

  public AbstractTest() {
    httpClient = new JettyHttpClient(5000, Optional.empty(), "test");
  }
}
