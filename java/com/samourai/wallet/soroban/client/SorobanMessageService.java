package com.samourai.wallet.soroban.client;

public abstract class SorobanMessageService<T extends SorobanMessage> {
    public abstract T parse(String payload) throws Exception;

    public abstract T reply(int account, T message) throws Exception;

    public T reply(int account, String payload) throws Exception {
        return reply(account, parse(payload));
    }
}
