package com.samourai.wallet.cahoots;

import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.send.UTXO;
import org.bitcoinj.core.ECKey;

import java.util.List;

public abstract class CahootsWallet {
    public abstract ECKey getPrivKey(String address, int account);
    public abstract String getUnspentPath(String address);
    public abstract List<UTXO> getCahootsUTXO(int account);
    public abstract int getHighestPostChangeIdx();
    public abstract BIP84Wallet getBip84Wallet();
}
