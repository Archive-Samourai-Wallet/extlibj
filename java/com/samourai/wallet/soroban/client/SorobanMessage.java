package com.samourai.wallet.soroban.client;

public interface SorobanMessage extends SorobanReply {
    String toPayload();

    boolean isDone();
}
