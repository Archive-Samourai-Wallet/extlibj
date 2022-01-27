package com.samourai.whirlpool.client.wallet.beans;

import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Arrays;

public enum WhirlpoolAccount {
  DEPOSIT(true),
  PREMIX(true),
  POSTMIX(true),
  BADBANK(false);

  private boolean active;

  WhirlpoolAccount(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  public static WhirlpoolAccount[] getListByActive(final boolean active) {
    return StreamSupport.stream(Arrays.asList(values()))
        .filter(
            new Predicate<WhirlpoolAccount>() {
              @Override
              public boolean test(WhirlpoolAccount account) {
                return account.isActive() == active;
              }
            })
        .collect(Collectors.<WhirlpoolAccount>toList())
        .toArray(new WhirlpoolAccount[] {});
  }
}
