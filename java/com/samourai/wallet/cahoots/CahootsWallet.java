package com.samourai.wallet.cahoots;

import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

import java.util.LinkedList;
import java.util.List;

public abstract class CahootsWallet {
    private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

    private BIP84Wallet bip84Wallet;
    private NetworkParameters params;

    public CahootsWallet(BIP84Wallet bip84Wallet, NetworkParameters params) {
        this.bip84Wallet = bip84Wallet;
        this.params = params;
    }

    public abstract int fetchPostChangeIndex();

    protected abstract List<CahootsUtxo> fetchUtxos(int account);

    public NetworkParameters getParams() {
        return params;
    }

    public BIP84Wallet getBip84Wallet() {
        return bip84Wallet;
    }

    public List<CahootsUtxo> getUtxosWpkhByAccount(int account) {
        return filterUtxosWpkh(fetchUtxos(account));
    }

    protected static List<CahootsUtxo> filterUtxosWpkh(List<CahootsUtxo> utxos) {
        List<CahootsUtxo> filteredUtxos = new LinkedList<CahootsUtxo>();
        for(CahootsUtxo utxo : utxos)   {
            // filter wpkh
            String script = Hex.toHexString(utxo.getOutpoint().getScriptBytes());
            if (bech32Util.isP2WPKHScript(script)) {
                filteredUtxos.add(utxo);
            }
        }
        return filteredUtxos;
    }
}
