package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BipFormatSupplier;

public interface UtxoKeyProvider {

    byte[] _getPrivKey(String utxoHash, int utxoIndex) throws Exception;

    BipFormatSupplier getBipFormatSupplier();
}
