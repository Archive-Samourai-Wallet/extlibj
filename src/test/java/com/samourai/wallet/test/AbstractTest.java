package com.samourai.wallet.test;

import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTest {
  protected static final Logger log = LoggerFactory.getLogger(AbstractTest.class);

  protected static final String SEED_WORDS = "all all all all all all all all all all all all";
  protected static final String SEED_PASSPHRASE = "whirlpool";

  protected NetworkParameters params = TestNet3Params.get();
  protected HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();
  protected Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

  public AbstractTest() {

  }
}
