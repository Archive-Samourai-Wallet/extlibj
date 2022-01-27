package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.send.UTXO;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.Collection;

public interface UtxoProvider extends UtxoKeyProvider {

    String getChangeAddress(WhirlpoolAccount account, BipFormat bipFormat);

    Collection<UTXO> getUtxos(WhirlpoolAccount account);

    Collection<UTXO> getUtxos(WhirlpoolAccount account, BipFormat bipFormat);
}
