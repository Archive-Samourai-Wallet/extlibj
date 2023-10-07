package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.send.UTXO;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.Collection;

public class SimpleCahootsUtxoProvider implements CahootsUtxoProvider {
    private UtxoProvider utxoProvider;

    public SimpleCahootsUtxoProvider(UtxoProvider utxoProvider) {
        this.utxoProvider = utxoProvider;
    }

    @Override
    public Collection<CahootsUtxo> getUtxosWpkhByAccount(int account) {
        WhirlpoolAccount whirlpoolAccount = SamouraiAccountIndex.find(account);
        Collection<UTXO> utxos = utxoProvider.getUtxos(whirlpoolAccount, BIP_FORMAT.SEGWIT_NATIVE);
        return CahootsUtxo.toCahootsUtxos(utxos, utxoProvider);
    }

    @Override
    public UtxoKeyProvider getUtxoKeyProvider() {
        return utxoProvider;
    }
}
