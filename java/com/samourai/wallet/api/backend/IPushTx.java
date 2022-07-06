package com.samourai.wallet.api.backend;

import org.apache.commons.lang3.tuple.Pair;

public interface IPushTx {
    Pair<Boolean,String> pushTx(String hexTx) throws Exception;
}
