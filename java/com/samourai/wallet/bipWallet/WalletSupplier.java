package com.samourai.wallet.bipWallet;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.Collection;

public interface WalletSupplier {
  Collection<BipWallet> getWallets();

  Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount);

  BipWallet getWallet(WhirlpoolAccount account, BipFormat bipFormat);

  BipWallet getWallet(BIP_WALLET bip);

  BipWallet getWalletByXPub(String pub);

  BipWallet getWalletById(String id);

  String[] getXPubs(boolean withIgnoredAccounts, BipFormat... bipFormats);

  BipFormatSupplier getBipFormatSupplier();
}
