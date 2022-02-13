package com.samourai.wallet.client;

import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BipAddressTest extends AbstractTest {
  private BipWallet bipWalletDeposit;
  private BipWallet bipWalletPremix;

  public BipAddressTest() throws Exception {
    super();
  }

  @BeforeEach
  public void setUp() throws Exception {
    byte[] seed = hdWalletFactory.computeSeedFromWords(SEED_WORDS);
    HD_Wallet bip44w = hdWalletFactory.getBIP44(seed, SEED_PASSPHRASE, params);
    bipWalletDeposit = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.DEPOSIT_BIP44);
    bipWalletPremix = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.POSTMIX_BIP84);
  }

  @Test
  public void getAddressString() throws Exception {
    Assertions.assertEquals(
        "mhJALds5qzDgZrpnAS9xk8utUcBEWmFdaU", bipWalletDeposit.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
        "mrdresnK7GvmSwQTKHCjV87ygEyi89EKGG", bipWalletDeposit.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
        "mvBRvDzdcwrAQ4rk6bQy3MMSz5J1GDJgDB", bipWalletDeposit.getAddressAt(1, 1).getAddressString());

    Assertions.assertEquals(
            "tb1qef2tq6prhk2u0a09pgllm73m5vl4q5qtcu2fp3", bipWalletPremix.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
            "tb1q6jfh0zp0mjfem9g655drczkkm49p2t3xlqyd69", bipWalletPremix.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
            "tb1qv7n3qjsn449nrm4q5hvlrpj6p2d077mmukjd3e", bipWalletPremix.getAddressAt(1, 1).getAddressString());

  }

  @Test
  public void getPathAddress() throws Exception {
    Assertions.assertEquals(
            "m/44'/0'/0'/0/0", bipWalletDeposit.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/44'/0'/0'/1/0", bipWalletDeposit.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/44'/0'/0'/1/1", bipWalletDeposit.getAddressAt(1, 1).getPathAddress());

    Assertions.assertEquals(
            "m/84'/0'/2147483646'/0/0", bipWalletPremix.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/0'/2147483646'/1/0", bipWalletPremix.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/0'/2147483646'/1/1", bipWalletPremix.getAddressAt(1, 1).getPathAddress());
  }
}