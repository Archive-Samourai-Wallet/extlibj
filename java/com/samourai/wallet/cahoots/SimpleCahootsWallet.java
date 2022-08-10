package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.WalletSupplier;
import org.bitcoinj.core.NetworkParameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleCahootsWallet extends CahootsWallet {
    private Map<Integer,List<CahootsUtxo>> utxosByAccount;

    public SimpleCahootsWallet(WalletSupplier walletSupplier, BipFormatSupplier bipFormatSupplier, NetworkParameters params) throws Exception {
        super(walletSupplier, bipFormatSupplier, params);
        this.utxosByAccount = new HashMap<Integer, List<CahootsUtxo>>();
    }

    @Override
    protected List<CahootsUtxo> fetchUtxos(int account) {
        return utxosByAccount.get(account);
    }

    public void addUtxo(int account, CahootsUtxo utxo) {
        if (!utxosByAccount.containsKey(account)) {
            utxosByAccount.put(account, new LinkedList<CahootsUtxo>());
        }
        utxosByAccount.get(account).add(utxo);
    }
}
