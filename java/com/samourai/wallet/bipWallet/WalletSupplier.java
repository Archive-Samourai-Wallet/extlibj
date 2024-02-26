package com.samourai.wallet.bipWallet;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.constants.WhirlpoolAccount;

import java.util.Collection;

public interface WalletSupplier {
  Collection<BipWallet> getWallets();

  Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount);

  BipWallet getWallet(WhirlpoolAccount account, BipFormat bipFormat);

  BipWallet getWallet(BIP_WALLET bip);

  BipWallet getWalletByPub(String pub);

  BipWallet getWalletById(String id);

  String[] getPubs(boolean withIgnoredAccounts, BipFormat... bipFormats);
}
