package com.samourai.dex.config;

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
  public void test() {
    Assertions.assertEquals("https://api.samouraiwallet.com/v2", dexConfigProvider.getSamouraiConfig().getBackendServerMainnetClear());
  }
}
