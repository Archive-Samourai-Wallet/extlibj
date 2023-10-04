package com.samourai.wallet.send.provider;

import com.samourai.wallet.cahoots.CahootsUtxo;

import java.util.Collection;

public interface CahootsUtxoProvider {
    Collection<CahootsUtxo> getUtxosWpkhByAccount(int account);
}
