package com.samourai.wallet.cahoots;

import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.provider.UtxoKeyProvider;
import org.bitcoinj.core.Coin;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CahootsUtxo extends UTXO {
    private MyTransactionOutPoint outpoint;
    private byte[] key;

    public CahootsUtxo(MyTransactionOutPoint cahootsOutpoint, String path, byte[] key) {
        super(new LinkedList<MyTransactionOutPoint>(Arrays.asList(new MyTransactionOutPoint[]{cahootsOutpoint})), path);
        this.outpoint = cahootsOutpoint;
        this.key = key;
    }

    public MyTransactionOutPoint getOutpoint() {
        return outpoint;
    }

    public byte[] getKey() {
        return key;
    }

    public static Coin sumValue(List<CahootsUtxo> utxos) {
        Coin balance = Coin.ZERO;
        for(CahootsUtxo cahootsUtxo : utxos) {
            balance = balance.add(cahootsUtxo.getOutpoint().getValue());
        }
        return balance;
    }

    public static List<CahootsUtxo> toCahootsUtxos(Collection<UTXO> utxos, UtxoKeyProvider keyProvider) {
        return utxos.stream().map(utxo -> {
            MyTransactionOutPoint outPoint = utxo.getOutpoints().get(0);
            byte[] key = null;
            try {
                key = keyProvider._getPrivKey(outPoint.getHash().toString(), (int) outPoint.getIndex());
            } catch (Exception e) {}
            return new CahootsUtxo(outPoint, utxo.getPath(), key);
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CahootsUtxo{" +
                "utxo=" + outpoint.toString() +
                ", value=" + outpoint.getValue() +
                ", address="+outpoint.getAddress() +
                ", path="+getPath()+
                '}';
    }
}
