package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import org.bitcoinj.core.ECKey;

public interface UtxoKeyProvider {

    ECKey _getPrivKey(String utxoHash, int utxoIndex) throws Exception;

    BipFormatSupplier getBipFormatSupplier();
}
