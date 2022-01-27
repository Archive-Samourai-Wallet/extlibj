package com.samourai.wallet.bipWallet;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.Collection;

public interface WalletSupplier {
  Collection<BipWallet> getWallets();

  Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount);

  BipWallet getWallet(WhirlpoolAccount account, BipFormat bipFormat);

  BipWallet getWalletByPub(String pub);

  BipWallet getWalletById(String id);

  String[] getPubs(boolean withIgnoredAccounts, BipFormat... bipFormats);
}
