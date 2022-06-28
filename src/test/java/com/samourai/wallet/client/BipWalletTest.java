package com.samourai.wallet.client;

import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.test.AbstractTest;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BipWalletTest extends AbstractTest {
  private BipWallet bipWallet;

  public BipWalletTest() throws Exception {
    super();
  }

  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();

    bipWallet = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);
  }

  @Test
  public void getAddressAt() throws Exception {
    Assertions.assertEquals(
        "tb1qp4jqz890g3u30meeks68aeqyf7tdaeycyc6hd0", bipWallet.getAddressAt(0, 0).getAddressString());
    Assertions.assertEquals(
        "tb1q7uef0jnnj2dnzguz438aeejpqhjk7z45ngd4ww", bipWallet.getAddressAt(0, 15).getAddressString());
    Assertions.assertEquals(
        "tb1q765gfuv0f4l83fqk0sl9vaeu8tjcuqtyrrduyv", bipWallet.getAddressAt(1, 0).getAddressString());
    Assertions.assertEquals(
        "tb1q80vm5fqpr4pmnje0ftqkmhfmeztm4ua08kry00", bipWallet.getAddressAt(1, 15).getAddressString());
  }

  @Test
  public void getNextAddress() throws Exception {
    Assertions.assertEquals(
        bipWallet.getAddressAt(0, 0).getAddressString(), bipWallet.getNextAddress().getAddressString());
    Assertions.assertEquals(
        bipWallet.getAddressAt(0, 1).getAddressString(), bipWallet.getNextAddress().getAddressString());
    Assertions.assertEquals(
        bipWallet.getAddressAt(0, 2).getAddressString(), bipWallet.getNextAddress().getAddressString());

    // change
    Assertions.assertEquals(
        bipWallet.getAddressAt(1, 0).getAddressString(), bipWallet.getNextChangeAddress().getAddressString());
    Assertions.assertEquals(
        bipWallet.getAddressAt(1, 1).getAddressString(), bipWallet.getNextChangeAddress().getAddressString());
    Assertions.assertEquals(
        bipWallet.getAddressAt(1, 2).getAddressString(), bipWallet.getNextChangeAddress().getAddressString());
  }

  @Test
  public void getZpub() throws Exception {
    Assertions.assertEquals(
        "vpub5YEQpEDPAZWVTkmWASSHyaUMsae7uV9FnRrhZ3cqV6RFbBQx7wjVsUfLqSE3hgNY8WQixurkbWNkfV2sRE7LPfNKQh2t3s5une4QZthwdCu",
        bipWallet.getPub());
  }

  @Test
  public void derivationGetPath() throws Exception {
    Assertions.assertEquals("m/84'/1'/0", bipWallet.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/0'/0", bipWallet.getDerivation().getPathChain(0, params));
    Assertions.assertEquals("m/84'/1'/0'/1", bipWallet.getDerivation().getPathChain(1, params));

    NetworkParameters mainnetParams = MainNetParams.get();
    Assertions.assertEquals("m/84'/0'/0", bipWallet.getDerivation().getPathAccount(mainnetParams));

    byte[] seed = hdWalletFactory.computeSeedFromWords(SEED_WORDS);
    HD_Wallet bip44w = hdWalletFactory.getBIP44(seed, SEED_PASSPHRASE, params);
    BipWallet bipWallet44 = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.DEPOSIT_BIP44);
    Assertions.assertEquals("m/44'/1'/0", bipWallet44.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/44'/1'/0'/2", bipWallet44.getDerivation().getPathChain(2, params));

    BipWallet bipWallet49 = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.DEPOSIT_BIP49);
    Assertions.assertEquals("m/49'/1'/0", bipWallet49.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/49'/1'/0'/2", bipWallet49.getDerivation().getPathChain(2, params));

    BipWallet bipWalletBadbank = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.BADBANK_BIP84);
    Assertions.assertEquals("m/84'/1'/2147483644", bipWalletBadbank.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483644'/2", bipWalletBadbank.getDerivation().getPathChain(2, params));

    BipWallet bipWalletPremix = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.PREMIX_BIP84);
    Assertions.assertEquals("m/84'/1'/2147483645", bipWalletPremix.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483645'/2", bipWalletPremix.getDerivation().getPathChain(2, params));

    BipWallet bipWalletPostmix = new BipWallet(bip44w, new MemoryIndexHandlerSupplier(), BIP_WALLET.POSTMIX_BIP84);
    Assertions.assertEquals("m/84'/1'/2147483646", bipWalletPostmix.getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483646'/2", bipWalletPostmix.getDerivation().getPathChain(2, params));
  }
}
