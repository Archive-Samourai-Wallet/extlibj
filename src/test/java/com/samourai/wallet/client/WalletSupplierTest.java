package com.samourai.wallet.client;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatImpl;
import com.samourai.wallet.bipWallet.BipDerivation;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.HD_Account;
import com.samourai.wallet.test.AbstractTest;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

public class WalletSupplierTest extends AbstractTest {
  private static final int NB_WALLETS = 7;

  private static final String XPUB_DEPOSIT_BIP44 = "tpubDC9KwqYcT3e9Kp4W72ByimMVpfVGbJB6vDUfhBDtjQvSJGVjnY5HE1yxE8cNPBJrCG72m6jRgCnvWeswZAcMtV3efHbWGBodmiaBmWSQLwY";
  private static final String XPUB_DEPOSIT_BIP49 = "tpubDCij3gmJac85LAd6kqYhos7HNtiNEKCMx9BfBXCACJ5X2B4xRDoSahNHc94hQvYmdaysrAyVGUAqtMTGaBRFegZaWJZnPm6MqhwFRArPeV2";
  private static final String XPUB_DEPOSIT_BIP84 =
      "tpubDCGZwoNuBCYuS9LbHLzdbfzjYe2fn7dKAHVSUPTkb1vuSfi7hUuiG3eT7tE1DzdcjhBF5SZk3vuu8EkcFUnbsaBpCyB2uDP7v3n774RGre9";
  private static final String XPUB_PREMIX_BIP84 =
      "tpubDCGZwoP3Ws5sUQb1uwYxqQfmEjiPfSGrBcomWLyYgYw7YP5LenJexEzxwHvJoYUQSCWZupgzcx91fr4wVdJCb21LTr6fcv4GvBio4bzAhvr";
  private static final String XPUB_POSTMIX_BIP84 =
      "tpubDCGZwoP3Ws5sZLQpXGpDhtbErQPyFdf59k8JmUpnL5fM6qAj8bbPXNwLLtfiS5s8ivZ1W1PQnaET7obFeiDSooTFBKcTweS29BkgHwhhsQD";
  private static final String XPUB_BADBANK_BIP84 = "tpubDCGZwoP3Ws5sShKPkYYj7h8irzrVpZKZkK97t88Kup5eGHuRJ56zt4tppwb75rg9ja8JYjBxNi19HF9wbJa9vFMAJCrLAE827hGbSNGNhmu";
  private static final String XPUB_RICOCHET_BIP84 = "tpubDCGZwoP3Ws5sae6ERSHSfZ6ZYpEmLbZt1ng2JFahnHqK23BrXZ9zmC5xzb27ectmDoXfJ3mRbQLCYTb3u4PU4kpad7JGCdeMfJUgvCcDUMc";

  @Test
  public void getWallet() throws Exception {
    Assertions.assertEquals("m/44'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP44).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/49'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP49).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483644", walletSupplier.getWallet(BIP_WALLET.BADBANK_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483645", walletSupplier.getWallet(BIP_WALLET.PREMIX_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483646", walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483647", walletSupplier.getWallet(BIP_WALLET.RICOCHET_BIP84).getDerivation().getPathAccount(params));
  }

  @Test
  public void getWalletByFormat() throws Exception {
    Assertions.assertEquals("DEPOSIT_BIP44", walletSupplier.getWallet(WhirlpoolAccount.DEPOSIT, BIP_FORMAT.LEGACY).getId());
    Assertions.assertEquals("DEPOSIT_BIP49", walletSupplier.getWallet(WhirlpoolAccount.DEPOSIT, BIP_FORMAT.SEGWIT_COMPAT).getId());
    Assertions.assertEquals("DEPOSIT_BIP84", walletSupplier.getWallet(WhirlpoolAccount.DEPOSIT, BIP_FORMAT.SEGWIT_NATIVE).getId());
    Assertions.assertEquals("BADBANK_BIP84", walletSupplier.getWallet(WhirlpoolAccount.BADBANK, BIP_FORMAT.SEGWIT_NATIVE).getId());
    Assertions.assertEquals("PREMIX_BIP84", walletSupplier.getWallet(WhirlpoolAccount.PREMIX, BIP_FORMAT.SEGWIT_NATIVE).getId());
    Assertions.assertEquals("POSTMIX_BIP84", walletSupplier.getWallet(WhirlpoolAccount.POSTMIX, BIP_FORMAT.SEGWIT_NATIVE).getId());
    Assertions.assertEquals("POSTMIX_BIP84", walletSupplier.getWallet(WhirlpoolAccount.POSTMIX, BIP_FORMAT.SEGWIT_COMPAT).getId());
    Assertions.assertEquals("POSTMIX_BIP84", walletSupplier.getWallet(WhirlpoolAccount.POSTMIX, BIP_FORMAT.LEGACY).getId());
    Assertions.assertEquals("RICOCHET_BIP84", walletSupplier.getWallet(WhirlpoolAccount.RICOCHET, BIP_FORMAT.SEGWIT_NATIVE).getId());
    Assertions.assertNull(walletSupplier.getWallet(WhirlpoolAccount.RICOCHET, BIP_FORMAT.LEGACY));
  }

  @Test
  public void getWalletByPub() throws Exception {
    Assertions.assertEquals("DEPOSIT_BIP44", walletSupplier.getWalletByXPub(XPUB_DEPOSIT_BIP44).getId());
    Assertions.assertEquals("DEPOSIT_BIP49", walletSupplier.getWalletByXPub(XPUB_DEPOSIT_BIP49).getId());
    Assertions.assertEquals("DEPOSIT_BIP84", walletSupplier.getWalletByXPub(XPUB_DEPOSIT_BIP84).getId());
    Assertions.assertEquals("PREMIX_BIP84", walletSupplier.getWalletByXPub(XPUB_PREMIX_BIP84).getId());
    Assertions.assertEquals("POSTMIX_BIP84", walletSupplier.getWalletByXPub(XPUB_POSTMIX_BIP84).getId());
    Assertions.assertEquals("RICOCHET_BIP84", walletSupplier.getWalletByXPub(XPUB_RICOCHET_BIP84).getId());
    Assertions.assertEquals("BADBANK_BIP84", walletSupplier.getWalletByXPub(XPUB_BADBANK_BIP84).getId());
  }

  @Test
  public void getWallets() throws Exception {
    Collection<BipWallet> wallets = walletSupplier.getWallets();
    Assertions.assertEquals(NB_WALLETS, wallets.size());
  }

  @Test
  public void getPubs() throws Exception {
    // all
    String[] pubs = walletSupplier.getXPubs(true);
    Assertions.assertEquals(XPUB_DEPOSIT_BIP44, pubs[0]);
    Assertions.assertEquals(XPUB_DEPOSIT_BIP49, pubs[1]);
    Assertions.assertEquals(XPUB_DEPOSIT_BIP84, pubs[2]);
    Assertions.assertEquals(XPUB_PREMIX_BIP84, pubs[3]);
    Assertions.assertEquals(XPUB_POSTMIX_BIP84, pubs[4]);
    Assertions.assertEquals(XPUB_BADBANK_BIP84, pubs[5]);
    Assertions.assertEquals(XPUB_RICOCHET_BIP84, pubs[6]);

    // all
    Assertions.assertEquals(NB_WALLETS, walletSupplier.getXPubs(true).length);
    Assertions.assertEquals(NB_WALLETS, walletSupplier.getXPubs(true, null).length);

    // actives
    Assertions.assertEquals(5, walletSupplier.getXPubs(false, null).length); // ignore badbank & ricochet

    // by format
    Assertions.assertEquals(2, walletSupplier.getXPubs(true, BIP_FORMAT.LEGACY).length);
    Assertions.assertEquals(2, walletSupplier.getXPubs(true, BIP_FORMAT.SEGWIT_COMPAT).length);
    Assertions.assertEquals(5, walletSupplier.getXPubs(true, BIP_FORMAT.SEGWIT_NATIVE).length);
  }

  @Test
  public void registerCustom() throws Exception {
    // register custom derivation
    BipDerivation derivation = new BipDerivation(123, 4);
    String bipFormatId = "test";
    BipFormat bipFormat = new BipFormatImpl(bipFormatId, "label") {

      @Override
      public String getBipPub(HD_Account hdAccount) {
        return "testpub-"+hdAccount.getId();
      }

      @Override
      public String getToAddress(ECKey ecKey, NetworkParameters params) {
        return "testaddress";
      }

      @Override
      public void sign(Transaction tx, int inputIndex, ECKey key) throws Exception {
        throw new Exception("Not implemented");
      }
    };
    Assertions.assertEquals(3, walletSupplier.getWallets(WhirlpoolAccount.DEPOSIT).size());
    walletSupplier.register("custom", bip44w, WhirlpoolAccount.DEPOSIT, derivation, Arrays.asList(bipFormat), bipFormat);
    Assertions.assertEquals(4, walletSupplier.getWallets(WhirlpoolAccount.DEPOSIT).size());

    // verify
    BipWallet bipWallet = walletSupplier.getWalletById("custom");
    Assertions.assertNotNull(bipFormat);
    Assertions.assertEquals("m/123'/1'/4", bipWallet.getDerivation().getPathAccount(params));
    Assertions.assertEquals("testpub-4", bipWallet.getBipPub());
    Assertions.assertEquals("testaddress", bipWallet.getNextAddressReceive().getAddressString());
  }
}
