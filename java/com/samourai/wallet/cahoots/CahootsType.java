package com.samourai.wallet.cahoots;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.samourai.wallet.send.beans.SpendType;

public enum CahootsType {
    STONEWALLX2(0, "StonewallX2", true, SpendType.CAHOOTS_STONEWALL2X),
    STOWAWAY(1, "Stowaway", false, SpendType.CAHOOTS_STOWAWAY),
    MULTI(2, "MultiCahoots", false, SpendType.CAHOOTS_MULTI);

    private int value;
    private String label;
    private boolean minerFeeShared;
    private SpendType spendType;

    CahootsType(int value, String label, boolean minerFeeShared, SpendType spendType) {
        this.value = value;
        this.label = label;
        this.minerFeeShared = minerFeeShared;
        this.spendType = spendType;
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

    public boolean isMinerFeeShared() {
        return minerFeeShared;
    }

    public SpendType getSpendType() {
        return spendType;
    }
}