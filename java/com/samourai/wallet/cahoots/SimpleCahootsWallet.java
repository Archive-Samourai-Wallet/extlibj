package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.send.provider.CahootsUtxoProvider;
import org.bitcoinj.core.NetworkParameters;

public class SimpleCahootsWallet extends CahootsWallet {
    public SimpleCahootsWallet(WalletSupplier walletSupplier, BipFormatSupplier bipFormatSupplier, NetworkParameters params, CahootsUtxoProvider utxoProvider, long feePerB) throws Exception {
        super(walletSupplier, bipFormatSupplier, params, utxoProvider);
    }
}
