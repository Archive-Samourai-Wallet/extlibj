package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.utxo.BipUtxo;

public interface UtxoKeyProvider {

    byte[] _getPrivKey(BipUtxo bipUtxo) throws Exception;

    BipFormatSupplier getBipFormatSupplier();
}
