package com.samourai.wallet.dexConfig;

import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DexConfigProviderTest extends AbstractTest {

  private DexConfigProvider dexConfigProvider = DexConfigProvider.getInstance();

  @BeforeEach
  public void setUp() throws Exception{
    super.setUp();
  }

  @Test
  public void getBackendServerMainnetClear() {
    Assertions.assertEquals("https://api.samouraiwallet.com/v2", dexConfigProvider.getSamouraiConfig().getBackendServerMainnetClear());
  }

  @Test
  public void load() throws Exception {
    Assertions.assertNull(dexConfigProvider.getLastLoad());
    dexConfigProvider.load(httpClient, params, false);
    Assertions.assertNotNull(dexConfigProvider.getLastLoad());

    Assertions.assertEquals("https://soroban.samouraiwallet.com/test", dexConfigProvider.getSamouraiConfig().getSorobanServerTestnetClear());
  }
}
