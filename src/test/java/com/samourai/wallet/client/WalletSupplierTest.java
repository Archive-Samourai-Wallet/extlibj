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

import java.util.Collection;

public class WalletSupplierTest extends AbstractTest {
  private static final int NB_WALLETS = 9;

  private static final String ZPUB_DEPOSIT_BIP84 =
      "vpub5YEQpEDPAZWVTkmWASSHyaUMsae7uV9FnRrhZ3cqV6RFbBQx7wjVsUfLqSE3hgNY8WQixurkbWNkfV2sRE7LPfNKQh2t3s5une4QZthwdCu";
  private static final String ZPUB_PREMIX_BIP84 =
      "vpub5YEQpEDXWE3TW21vo2zdDK9PZgKqnonnomB2b18dadRTgtnB5F8SZg1reqvMHEDKq1k3oHz1AXbsD6MCfNcw77BqfZxWmZm4nn16XNC84mL";
  private static final String ZPUB_POSTMIX_BIP84 =
      "vpub5YEQpEDXWE3TawqjQNFt5o4sBM1RP1B1mtVZr8ysEA9hFLsZZ4RB8oxE4Sfkumc47jnVPUgRL9hJf3sWpTYBKtdkP3UK6J8p1n2ykmjHnrW";
  private static final String ZPUB_POSTMIX_BIP84_AS_BIP49 = "upub5DQ9WZYcMYVyjeeca1UFshyN1NrySPBWrmyM4k5yr9mpCF4LJQFcWkJ63EiAurx8i6fge15rsVLkmmFx6m8AXex9WhmtWPKKk3yLN6dDTKP";

  @Test
  public void getWallet() throws Exception {
    Assertions.assertEquals("m/44'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP44).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/49'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP49).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/0", walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483644", walletSupplier.getWallet(BIP_WALLET.BADBANK_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483645", walletSupplier.getWallet(BIP_WALLET.PREMIX_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483646", walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483646", walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84_AS_BIP49).getDerivation().getPathAccount(params));
    Assertions.assertEquals("m/84'/1'/2147483646", walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84_AS_BIP44).getDerivation().getPathAccount(params));
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
    Assertions.assertEquals("POSTMIX_BIP84_AS_BIP49", walletSupplier.getWallet(WhirlpoolAccount.POSTMIX, BIP_FORMAT.SEGWIT_COMPAT).getId());
    Assertions.assertEquals("POSTMIX_BIP84_AS_BIP44", walletSupplier.getWallet(WhirlpoolAccount.POSTMIX, BIP_FORMAT.LEGACY).getId());
    Assertions.assertEquals("RICOCHET_BIP84", walletSupplier.getWallet(WhirlpoolAccount.RICOCHET, BIP_FORMAT.SEGWIT_NATIVE).getId());
  }

  @Test
  public void getWalletByPub() throws Exception {
    Assertions.assertEquals("DEPOSIT_BIP84", walletSupplier.getWalletByPub(ZPUB_DEPOSIT_BIP84).getId());
    Assertions.assertEquals("PREMIX_BIP84", walletSupplier.getWalletByPub(ZPUB_PREMIX_BIP84).getId());
    Assertions.assertEquals("POSTMIX_BIP84", walletSupplier.getWalletByPub(ZPUB_POSTMIX_BIP84).getId());
    Assertions.assertEquals("POSTMIX_BIP84_AS_BIP49", walletSupplier.getWalletByPub(ZPUB_POSTMIX_BIP84_AS_BIP49).getId());
  }

  @Test
  public void getWallets() throws Exception {
    Collection<BipWallet> wallets = walletSupplier.getWallets();
    Assertions.assertEquals(NB_WALLETS, wallets.size());
  }

  @Test
  public void getPubs() throws Exception {
    // all
    String[] pubs = walletSupplier.getPubs(true);
    Assertions.assertEquals("tpubDC9KwqYcT3e9Kp4W72ByimMVpfVGbJB6vDUfhBDtjQvSJGVjnY5HE1yxE8cNPBJrCG72m6jRgCnvWeswZAcMtV3efHbWGBodmiaBmWSQLwY", pubs[0]); //DEPOSIT_BIP44
    Assertions.assertEquals("upub5DrJcSvsRHYBWUrtoaCjygVQXsBNR4iofB2hUnTMiNBz7axZb2Tfa4j3JV79thdmcm6YzAfwMPH9YK7y2EKyNY4UqgjCxVyfSa9uVUb938D", pubs[1]); //DEPOSIT_BIP49
    Assertions.assertEquals(ZPUB_DEPOSIT_BIP84, pubs[2]); //DEPOSIT_BIP84
    Assertions.assertEquals(ZPUB_PREMIX_BIP84, pubs[3]); //PREMIX_BIP84
    Assertions.assertEquals(ZPUB_POSTMIX_BIP84, pubs[4]); //POSTMIX_BIP84
    Assertions.assertEquals(ZPUB_POSTMIX_BIP84_AS_BIP49, pubs[5]); //POSTMIX_BIP84_AS_BIP49
    Assertions.assertEquals("tpubDCGZwoP3Ws5sZLQpXGpDhtbErQPyFdf59k8JmUpnL5fM6qAj8bbPXNwLLtfiS5s8ivZ1W1PQnaET7obFeiDSooTFBKcTweS29BkgHwhhsQD", pubs[6]); //POSTMIX_BIP84_AS_BIP44
    Assertions.assertEquals("vpub5YEQpEDXWE3TUJkJddzPVbcMBwTwwvqWNTWNxnHQotZzQocFiXvnVVuiYVb9ZYR58PMnSCUxvHTzpVSCm3ttSLXfVviBJspozHYtu6oNtNY", pubs[7]); //BADBANK_BIP84
    Assertions.assertEquals("vpub5YEQpEDXWE3TcFX9JXj73TaBskrDTy5pdw3HNujngNKfAYtgx1ynNd6ri92A8Jdgccm9BX4S8yo45hsK4oiCar15pqA7MHM9XtkzNySdknj", pubs[8]); //RICOCHET_BIP84

    // all
    Assertions.assertEquals(NB_WALLETS, walletSupplier.getPubs(true).length);
    Assertions.assertEquals(NB_WALLETS, walletSupplier.getPubs(true, null).length);

    // actives
    Assertions.assertEquals(7, walletSupplier.getPubs(false, null).length);

    // by format
    Assertions.assertEquals(2, walletSupplier.getPubs(true, BIP_FORMAT.LEGACY).length);
    Assertions.assertEquals(2, walletSupplier.getPubs(true, BIP_FORMAT.SEGWIT_COMPAT).length);
    Assertions.assertEquals(5, walletSupplier.getPubs(true, BIP_FORMAT.SEGWIT_NATIVE).length);

  }

  @Test
  public void registerCustom() throws Exception {
    // register custom derivation
    BipDerivation derivation = new BipDerivation(123, 4);
    String bipFormatId = "test";
    BipFormat bipFormat = new BipFormatImpl(bipFormatId, "label") {

      @Override
      public String getPub(HD_Account hdAccount) {
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
    walletSupplier.register("custom", bip44w, WhirlpoolAccount.DEPOSIT, derivation, bipFormat);
    Assertions.assertEquals(4, walletSupplier.getWallets(WhirlpoolAccount.DEPOSIT).size());

    // verify
    BipWallet bipWallet = walletSupplier.getWalletById("custom");
    Assertions.assertNotNull(bipFormat);
    Assertions.assertEquals("m/123'/1'/4", bipWallet.getDerivation().getPathAccount(params));
    Assertions.assertEquals("testpub-4", bipWallet.getPub());
    Assertions.assertEquals("testaddress", bipWallet.getNextAddress().getAddressString());
  }
}
