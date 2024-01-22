package com.samourai.whirlpool.client.wallet.beans;

public class SamouraiAccountIndex {
  public static final int DEPOSIT = 0;
  public static final int PREMIX = Integer.MAX_VALUE - 2;
  public static final int POSTMIX = Integer.MAX_VALUE - 1;
  public static final int BADBANK = Integer.MAX_VALUE - 3;
  public static final int RICOCHET = Integer.MAX_VALUE;
  public static final int SWAPS_ASB = 2147483641;
  public static final int SWAPS_REFUNDS = 2147483642;
  public static final int SWAPS_DEPOSIT = 2147483643;

  public static int find(WhirlpoolAccount whirlpoolAccount) {
    switch (whirlpoolAccount) {
      case PREMIX: return PREMIX;
      case POSTMIX: return POSTMIX;
      case BADBANK: return BADBANK;
      case RICOCHET: return RICOCHET;
      case SWAPS_ASB: return SWAPS_ASB;
      case SWAPS_DEPOSIT: return SWAPS_DEPOSIT;
      case SWAPS_REFUNDS: return SWAPS_REFUNDS;
      default: return DEPOSIT;
    }
  }

  @Deprecated // TODO accountIndex should not
  public static WhirlpoolAccount find(int accountIndex) {
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      if (find(whirlpoolAccount) == accountIndex) {
        return whirlpoolAccount;
      }
    }
    return null;
  }
}
