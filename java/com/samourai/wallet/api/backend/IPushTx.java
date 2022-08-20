package com.samourai.wallet.api.backend;

public interface IPushTx {
    String pushTx(String hexTx) throws Exception;
}
