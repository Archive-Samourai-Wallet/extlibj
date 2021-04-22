package com.samourai.wallet.hd;

import com.google.common.base.Optional;

public enum AddressType {
    LEGACY("Original (P2PKH)", 44),
    SEGWIT_COMPAT("Segwit compatible (P2SH)", 49),
    SEGWIT_NATIVE("Segwit native (bech32)", 84);

    private String label;
    private int purpose;

    AddressType(String label, int purpose) {
        this.label = label;
        this.purpose = purpose;
    }

    public static Optional<AddressType> findByPurpose(int purpose) {
        for (AddressType item : AddressType.values()) {
            if (item.purpose == purpose) {
                return Optional.of(item);
            }
        }
        return Optional.absent();
    }

    public String getLabel() {
        return label;
    }

    public int getPurpose() {
        return purpose;
    }
}
