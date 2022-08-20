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
  private BipWallet bipWalletPostmix;
  private BipWallet bipWalletPostmixAsBip44;
  private BipWallet bipWalletPostmixAsBip49;

  public BipAddressTest() throws Exception {
    super();
  }

  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();

    bipWalletDeposit = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP44);
    bipWalletPremix = walletSupplier.getWallet(BIP_WALLET.PREMIX_BIP84);
    bipWalletPostmix = walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84);
    bipWalletPostmixAsBip44 = walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84_AS_BIP44);
    bipWalletPostmixAsBip49 = walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84_AS_BIP49);
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
            "tb1qrhl7dcu4e9vj0e9pd68xh4ks2czygljdlpfkdm", bipWalletPremix.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
            "tb1qrd0gj9eqnpsqvwl2tr2jq08m0v0ujyh0r9fw7a", bipWalletPremix.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
            "tb1qhxwf7pj23nxwzfhusm9t0n26e3znj3sq0gu8w6", bipWalletPremix.getAddressAt(1, 1).getAddressString());

    Assertions.assertEquals(
            "tb1qef2tq6prhk2u0a09pgllm73m5vl4q5qtcu2fp3", bipWalletPostmix.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
            "tb1q6jfh0zp0mjfem9g655drczkkm49p2t3xlqyd69", bipWalletPostmix.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
            "tb1qv7n3qjsn449nrm4q5hvlrpj6p2d077mmukjd3e", bipWalletPostmix.getAddressAt(1, 1).getAddressString());

    Assertions.assertEquals(
            "myxnF72uWYFan5RR4fLKVKeiAbrQ8qQ3F9", bipWalletPostmixAsBip44.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
            "mztxCuMNZ2ZwTmMTiZgjp2aiyKZUnXwyMv", bipWalletPostmixAsBip44.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
            "mpy22barYekLsVEEeZMLP2vaEKCtDJsdB7", bipWalletPostmixAsBip44.getAddressAt(1, 1).getAddressString());

    Assertions.assertEquals(
            "2N48pFXu5Emo5XRkyNhCzx7pghCcnRJR9Mh", bipWalletPostmixAsBip49.getAddressAt(0, 0).getAddressString());

    Assertions.assertEquals(
            "2N4Y7AQ1bWADjquT831zg9Eg65AygAYi9Mq", bipWalletPostmixAsBip49.getAddressAt(1, 0).getAddressString());

    Assertions.assertEquals(
            "2NAhUx9Xyhgbaba864Dzg16fVhUUkr4ueST", bipWalletPostmixAsBip49.getAddressAt(1, 1).getAddressString());

  }

  @Test
  public void getPathAddress() throws Exception {
    Assertions.assertEquals(
            "m/44'/1'/0'/0/0", bipWalletDeposit.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/44'/1'/0'/1/0", bipWalletDeposit.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/44'/1'/0'/1/1", bipWalletDeposit.getAddressAt(1, 1).getPathAddress());

    Assertions.assertEquals(
            "m/84'/1'/2147483645'/0/0", bipWalletPremix.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483645'/1/0", bipWalletPremix.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483645'/1/1", bipWalletPremix.getAddressAt(1, 1).getPathAddress());

    Assertions.assertEquals(
            "m/84'/1'/2147483646'/0/0", bipWalletPostmix.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/0", bipWalletPostmix.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/1", bipWalletPostmix.getAddressAt(1, 1).getPathAddress());

    Assertions.assertEquals(
            "m/84'/1'/2147483646'/0/0", bipWalletPostmixAsBip44.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/0", bipWalletPostmixAsBip44.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/1", bipWalletPostmixAsBip44.getAddressAt(1, 1).getPathAddress());

    Assertions.assertEquals(
            "m/84'/1'/2147483646'/0/0", bipWalletPostmixAsBip49.getAddressAt(0, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/0", bipWalletPostmixAsBip49.getAddressAt(1, 0).getPathAddress());
    Assertions.assertEquals(
            "m/84'/1'/2147483646'/1/1", bipWalletPostmixAsBip49.getAddressAt(1, 1).getPathAddress());
  }
}
