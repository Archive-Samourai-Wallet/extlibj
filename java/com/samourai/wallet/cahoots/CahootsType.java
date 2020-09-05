package com.samourai.wallet.cahoots;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;

public enum CahootsType {
    STONEWALLX2(0, CahootsTypeUser.COUNTERPARTY),
    STOWAWAY(1, CahootsTypeUser.RECEIVER);

    private int value;
    private CahootsTypeUser typeUserCounterparty;

    CahootsType(int value, CahootsTypeUser typeUserCounterparty) {
        this.value = value;
        this.typeUserCounterparty = typeUserCounterparty;
    }

    public static Optional<CahootsType> find(int value) {
      for (CahootsType item : CahootsType.values()) {
          if (item.value == value) {
              return Optional.of(item);
          }
      }
      return Optional.absent();
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public CahootsTypeUser getTypeUserCounterparty() {
        return typeUserCounterparty;
    }
}