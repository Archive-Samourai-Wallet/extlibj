package com.samourai.whirlpool.client.wallet.beans;

public class SamouraiAccountIndex {
  public static final int DEPOSIT = 0;
  public static final int PREMIX = Integer.MAX_VALUE - 2;
  public static final int POSTMIX = Integer.MAX_VALUE - 1;
  public static final int BADBANK = Integer.MAX_VALUE - 3;
  public static final int RICOCHET = Integer.MAX_VALUE;

  public static int find(WhirlpoolAccount whirlpoolAccount) {
    switch (whirlpoolAccount) {
      case PREMIX: return PREMIX;
      case POSTMIX: return POSTMIX;
      case BADBANK: return BADBANK;
      case RICOCHET: return RICOCHET;
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
