package com.samourai.wallet.sorobanClient;

public interface SorobanMessage extends SorobanReply {
    String toPayload();

    boolean isDone();
}
