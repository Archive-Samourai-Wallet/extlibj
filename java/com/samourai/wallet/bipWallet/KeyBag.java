package com.samourai.wallet.bipWallet;

import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.provider.UtxoKeyProvider;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.BipUtxo;
import com.samourai.wallet.utxo.UtxoRef;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutPoint;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeyBag {
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
    private final Map<String,byte[]> privKeys;

    public KeyBag() {
        this.privKeys = new LinkedHashMap<>();
    }

    public void add(UtxoRef utxo, byte[] privKeyBytes) {
        String hashKey = utxoUtil.utxoToKey(utxo);
        add(hashKey, privKeyBytes);
    }

    public void add(TransactionOutPoint outpoint, byte[] privKeyBytes) {
        String hashKey = utxoUtil.utxoToKey(outpoint);
        add(hashKey, privKeyBytes);
    }

    public void add(BipUtxo bipUtxo, UtxoKeyProvider utxoKeyProvider) throws Exception {
        byte[] privKeyBytes = utxoKeyProvider._getPrivKey(bipUtxo);
        add(bipUtxo, privKeyBytes);
    }

    public void addAll(UTXO utxo, UtxoKeyProvider utxoKeyProvider) throws Exception {
        for (BipUtxo bipUtxo : utxo.toBipUtxos()) {
            add(bipUtxo, utxoKeyProvider);
        }
    }

    public void addAllUtxos(Collection<UTXO> utxos, UtxoKeyProvider utxoKeyProvider) throws Exception {
        for (UTXO utxo : utxos) {
            addAll(utxo, utxoKeyProvider);
        }
    }

    public void addAll(Collection<BipUtxo> unspentOutputs, UtxoKeyProvider utxoKeyProvider) throws Exception {
        for (BipUtxo unspentOutput : unspentOutputs) {
            add(unspentOutput, utxoKeyProvider);
        }
    }

    public void addAll(UTXO utxo, KeyBag keyBag) {
        for (BipUtxo bipUtxo : utxo.toBipUtxos()) {
            add(bipUtxo, keyBag.getPrivKeyBytes(bipUtxo));
        }
    }

    public byte[] getPrivKeyBytes(UtxoRef utxo) {
        String hashKey = utxoUtil.utxoToKey(utxo);
        return privKeys.get(hashKey);
    }

    public byte[] getPrivKeyBytes(TransactionOutPoint outPoint) {
        String hashKey = utxoUtil.utxoToKey(outPoint);
        return privKeys.get(hashKey);
    }

    public ECKey getECKey(UtxoRef utxo) {
        return getECKey(getPrivKeyBytes(utxo));
    }

    public ECKey getECKey(TransactionOutPoint outPoint) {
        return getECKey(getPrivKeyBytes(outPoint));
    }

    public Map<String, ECKey> toMap() {
        Map<String,ECKey> kb = new LinkedHashMap<>();
        for (Map.Entry<String,byte[]> e : privKeys.entrySet()) {
            kb.put(e.getKey(), ECKey.fromPrivate(e.getValue()));
        }
        return kb;
    }

    public int size() {
        return privKeys.size();
    }

    protected void add(String hashKey, byte[] privKeyBytes) {
        if (privKeyBytes == null) {
            throw new RuntimeException("privKey not found for utxo: "+hashKey);
        }
        this.privKeys.put(hashKey, privKeyBytes);
    }

    protected ECKey getECKey(byte[] privKeyBytes) {
        return privKeyBytes != null ? ECKey.fromPrivate(privKeyBytes) : null;
    }

    @Override
    public String toString() {
        return "KeyBag{keySet=" +
                privKeys.keySet() +
                '}';
    }
}
