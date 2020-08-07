package com.samourai.wallet.cahoots;

import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.send.UTXO;
import org.bitcoinj.core.ECKey;

import java.util.List;

public abstract class CahootsWallet {
    abstract ECKey getPrivKey(String address, int account);
    abstract String getUnspentPath(String address);
    abstract List<UTXO> getCahootsUTXO(int account);
    abstract long getFeePerB();
    abstract int getHighestPostChangeIdx();
    abstract HD_Wallet getHdWallet();
    abstract int getAccount();
}
