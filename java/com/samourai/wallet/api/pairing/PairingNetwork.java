package com.samourai.wallet.api.pairing;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolNetwork;

public enum PairingNetwork {
    MAINNET("mainnet", WhirlpoolNetwork.MAINNET),
    TESTNET("testnet", WhirlpoolNetwork.TESTNET);

    private String value;
    private WhirlpoolNetwork whirlpoolNetwork;

    PairingNetwork(String value, WhirlpoolNetwork whirlpoolNetwork) {
        this.value = value;
        this.whirlpoolNetwork = whirlpoolNetwork;
    }

    public static Optional<PairingNetwork> find(String value) {
        for (PairingNetwork item : PairingNetwork.values()) {
            if (item.value.equals(value)) {
                return Optional.of(item);
            }
        }
        return Optional.absent();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public WhirlpoolNetwork getWhirlpoolNetwork() {
        return whirlpoolNetwork;
    }
}