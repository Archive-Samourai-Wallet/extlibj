package com.samourai.wallet.cahoots;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;

public enum CahootsType {
    STONEWALLX2(0, "StonewallX2"),
    STOWAWAY(1, "Stowaway");

    private int value;
    private String label;

    CahootsType(int value, String label) {
        this.value = value;
        this.label = label;
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

    public String getLabel() {
        return label;
    }
}