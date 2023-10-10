package com.samourai.wallet.hd;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipWallet.BipDerivation;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BIP_WALLET {

  DEPOSIT_BIP44(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_44, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.LEGACY),
  DEPOSIT_BIP49(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_49, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.SEGWIT_COMPAT),
  DEPOSIT_BIP84(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.SEGWIT_NATIVE),
  PREMIX_BIP84(WhirlpoolAccount.PREMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.PREMIX), BIP_FORMAT.SEGWIT_NATIVE),
  POSTMIX_BIP84(WhirlpoolAccount.POSTMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.POSTMIX), BIP_FORMAT.SEGWIT_NATIVE),
  POSTMIX_BIP84_AS_BIP49(WhirlpoolAccount.POSTMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.POSTMIX), BIP_FORMAT.SEGWIT_COMPAT),
  POSTMIX_BIP84_AS_BIP44(WhirlpoolAccount.POSTMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.POSTMIX), BIP_FORMAT.LEGACY),
  ASB_BIP84(WhirlpoolAccount.SWAPS_ASB, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.SWAPS_ASB), BIP_FORMAT.SEGWIT_NATIVE),
  SWAPS_DEPOSIT(WhirlpoolAccount.SWAPS_DEPOSIT, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.SWAPS_DEPOSIT), BIP_FORMAT.SEGWIT_NATIVE),
  SWAPS_REFUNDS(WhirlpoolAccount.SWAPS_REFUNDS, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.SWAPS_REFUNDS), BIP_FORMAT.SEGWIT_NATIVE),

  BADBANK_BIP84(WhirlpoolAccount.BADBANK, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.BADBANK), BIP_FORMAT.SEGWIT_NATIVE),
  RICOCHET_BIP84(WhirlpoolAccount.RICOCHET, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.RICOCHET), BIP_FORMAT.SEGWIT_NATIVE);

  private static final Logger log = LoggerFactory.getLogger(BIP_WALLET.class);
  private WhirlpoolAccount account;
  private BipDerivation bipDerivation;
  private BipFormat bipFormat;

  BIP_WALLET(WhirlpoolAccount account, BipDerivation bipDerivation, BipFormat bipFormat) {
    this.account = account;
    this.bipDerivation = bipDerivation;
    this.bipFormat = bipFormat;
  }

  public WhirlpoolAccount getAccount() {
    return account;
  }

  public BipDerivation getBipDerivation() {
    return bipDerivation;
  }

  public BipFormat getBipFormat() {
    return bipFormat;
  }
}
