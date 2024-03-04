package com.samourai.wallet.constants;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.BipWalletSupplier;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.hd.HD_Wallet;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum BIP_WALLETS implements BipWalletSupplier {
  WHIRLPOOL(WhirlpoolAccount.DEPOSIT, WhirlpoolAccount.PREMIX, WhirlpoolAccount.POSTMIX, WhirlpoolAccount.BADBANK),
  WALLET(WhirlpoolAccount.DEPOSIT, WhirlpoolAccount.PREMIX, WhirlpoolAccount.POSTMIX, WhirlpoolAccount.BADBANK,
          WhirlpoolAccount.RICOCHET),
  SWAPS(WhirlpoolAccount.SWAPS_ASB, WhirlpoolAccount.SWAPS_DEPOSIT, WhirlpoolAccount.SWAPS_REFUNDS);

  private WhirlpoolAccount[] accounts;

  BIP_WALLETS(WhirlpoolAccount... accounts) {
    this.accounts = accounts;
  }

  public BIP_WALLET[] getBIP_WALLETS() {
    return BIP_WALLET.getListByAccounts(accounts);
  }

  @Override
  public Collection<BipWallet> getBipWallets(BipFormatSupplier bipFormatSupplier, HD_Wallet bip44w, IndexHandlerSupplier indexHandlerSupplier) {
    return Arrays.stream(getBIP_WALLETS()).map(bip_wallet ->
                bip_wallet.newBipWallet(bipFormatSupplier, bip44w, indexHandlerSupplier))
            .collect(Collectors.toList());
  }

  public WhirlpoolAccount[] getAccounts() {
    return accounts;
  }
}
