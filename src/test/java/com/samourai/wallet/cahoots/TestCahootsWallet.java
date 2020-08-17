package com.samourai.wallet.cahoots;

import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class TestCahootsWallet extends CahootsWallet {
    private BIP84Wallet bip84Wallet;
    private List<UTXO> utxos;
    private NetworkParameters params;

    public TestCahootsWallet(BIP84Wallet bip84Wallet, NetworkParameters params) {
        this.bip84Wallet = bip84Wallet;
        this.utxos = new LinkedList<UTXO>();
        this.params = params;
    }

    @Override
    public BIP84Wallet getBip84Wallet() {
        return bip84Wallet;
    }

    @Override
    public ECKey getPrivKey(String address, int account) {
        return ECKey.fromPrivate(BigInteger.valueOf(1234));
    }

    @Override
    public String getUnspentPath(String address) {
        return "M/0/1";
    }

    @Override
    public List<UTXO> getCahootsUTXO(int account) {
        return utxos;
    }

    public void mockUTXO(String txid, int n, long value, String address) {
        UTXO utxo = new UTXO();
        MyTransactionOutPoint outpoint = new MyTransactionOutPoint(params, Sha256Hash.of(txid.getBytes()), n, BigInteger.valueOf(value), null, address);
        outpoint.setConfirmations(999);
        utxo.getOutpoints().add(outpoint);
        utxos.add(utxo);
    }

    @Override
    public int getHighestPostChangeIdx() {
        return 123;
    }
}
