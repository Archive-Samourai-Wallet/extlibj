package com.samourai.wallet.utxo;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BipAddress;
import org.bitcoinj.core.NetworkParameters;

import java.util.Collection;

public interface BipUtxo extends UtxoDetail {
    boolean isBip47();
    Integer getChainIndex();
    Integer getAddressIndex();
    byte[] getScriptBytes();

    BipWallet getBipWallet(WalletSupplier walletSupplier);
    BipAddress getBipAddress(WalletSupplier walletSupplier);
    BipFormat getBipFormat(BipFormatSupplier bipFormatSupplier, NetworkParameters params);

    static long sumValue(Collection<? extends BipUtxo> utxos) {
        return UtxoDetail.sumValue(utxos);
    }
}
