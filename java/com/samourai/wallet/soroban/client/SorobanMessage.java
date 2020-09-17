package com.samourai.wallet.soroban.client;

public interface SorobanMessage {
    String toPayload();

    boolean isDone();

    boolean isInteraction();
}
