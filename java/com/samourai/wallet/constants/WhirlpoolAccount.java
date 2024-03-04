package com.samourai.wallet.constants;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum WhirlpoolAccount {
  DEPOSIT(true),
  PREMIX(true),
  POSTMIX(true),
  BADBANK(false),
  RICOCHET(false),
  SWAPS_ASB(true),
  SWAPS_DEPOSIT(true),
  SWAPS_REFUNDS(true);

  private boolean active;

  WhirlpoolAccount(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  public static WhirlpoolAccount[] getListByActive(final boolean active) {
    return Arrays.asList(values()).stream()
        .filter(account -> account.isActive() == active)
        .toArray(i -> new WhirlpoolAccount[i]);
  }
}
