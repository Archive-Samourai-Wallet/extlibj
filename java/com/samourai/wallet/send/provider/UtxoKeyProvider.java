package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BipFormatSupplier;

public interface UtxoKeyProvider {

    byte[] _getPrivKey(String utxoHash, int utxoIndex) throws Exception;
    byte[] _getPrivKeyBip47(String utxoHash, int utxoIndex, String xpub) throws Exception;

    BipFormatSupplier getBipFormatSupplier();
}
