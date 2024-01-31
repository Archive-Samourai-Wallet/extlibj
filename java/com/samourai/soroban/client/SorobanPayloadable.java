package com.samourai.soroban.client;

public interface SorobanPayloadable extends SorobanReply {
    String toPayload() throws Exception;
}
