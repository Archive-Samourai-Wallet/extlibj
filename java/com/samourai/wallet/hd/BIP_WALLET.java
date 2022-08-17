package com.samourai.wallet.hd;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipWallet.BipDerivation;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum BIP_WALLET {

  DEPOSIT_BIP44(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_44, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.LEGACY),
  DEPOSIT_BIP49(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_49, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.SEGWIT_COMPAT),
  DEPOSIT_BIP84(WhirlpoolAccount.DEPOSIT, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.DEPOSIT), BIP_FORMAT.SEGWIT_NATIVE),
  PREMIX_BIP84(WhirlpoolAccount.PREMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.PREMIX), BIP_FORMAT.SEGWIT_NATIVE),
  POSTMIX_BIP84(WhirlpoolAccount.POSTMIX, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.POSTMIX), List.of(BIP_FORMAT.SEGWIT_NATIVE, BIP_FORMAT.SEGWIT_COMPAT, BIP_FORMAT.LEGACY)),

  BADBANK_BIP84(WhirlpoolAccount.BADBANK, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.BADBANK), BIP_FORMAT.SEGWIT_NATIVE),
  RICOCHET_BIP84(WhirlpoolAccount.RICOCHET, new BipDerivation(Purpose.PURPOSE_84, SamouraiAccountIndex.RICOCHET), BIP_FORMAT.SEGWIT_NATIVE);

  private static final Logger log = LoggerFactory.getLogger(BIP_WALLET.class);
  private WhirlpoolAccount account;
  private BipDerivation bipDerivation;
  private List<BipFormat> bipFormats;

  BIP_WALLET(WhirlpoolAccount account, BipDerivation bipDerivation, BipFormat bipFormat) {
    this.account = account;
    this.bipDerivation = bipDerivation;
    this.bipFormats = new ArrayList<>(Collections.singleton(bipFormat));
  }

  BIP_WALLET(WhirlpoolAccount account, BipDerivation bipDerivation, List<BipFormat> bipFormats) {
    this.account = account;
    this.bipDerivation = bipDerivation;
    this.bipFormats = bipFormats;
  }

  public WhirlpoolAccount getAccount() {
    return account;
  }

  public BipDerivation getBipDerivation() {
    return bipDerivation;
  }

  public List<BipFormat> getBipFormats() {
    return bipFormats;
  }
}
