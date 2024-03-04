package com.samourai.wallet.bipWallet;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.constants.BIP_WALLET;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.constants.WhirlpoolAccount;

import java.util.Collection;

public interface WalletSupplier {
  Collection<BipWallet> getWallets();

  Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount);

  BipWallet getWallet(WhirlpoolAccount account, BipFormat bipFormat);

  BipWallet getWallet(BIP_WALLET bip);

  BipWallet getWallet(UnspentOutput unspentOutput);

  BipAddress getAddress(UnspentOutput unspentOutput);

  BipWallet getWalletByXPub(String pub);

  BipWallet getWalletById(String id);

  String[] getXPubs(boolean withIgnoredAccounts, BipFormat... bipFormats);
}
