package com.samourai.wallet.bipWallet;

import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.utxo.BipUtxo;
import org.bitcoinj.core.ECKey;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeyBag {
    private final Map<String,byte[]> privKeys;

    public KeyBag() {
        this.privKeys = new LinkedHashMap<>();
    }

    public void add(BipUtxo unspentOutput, byte[] privKeyBytes) {
        String hashKey = hashKey(unspentOutput);
        this.privKeys.put(hashKey, privKeyBytes);
    }

    public void add(BipUtxo unspentOutput, WalletSupplier walletSupplier) throws Exception {
        BipAddress bipAddress = unspentOutput.getBipAddress(walletSupplier);
        if (bipAddress == null) {
            throw new Exception("BipAddress not found for utxo: "+unspentOutput);
        }
        byte[] privKeyBytes = bipAddress.getHdAddress().getECKey().getPrivKeyBytes();
        add(unspentOutput, privKeyBytes);
    }

    public void addAll(Collection<BipUtxo> unspentOutputs, WalletSupplier walletSupplier) throws Exception {
        for (BipUtxo unspentOutput : unspentOutputs) {
            BipAddress bipAddress = unspentOutput.getBipAddress(walletSupplier);
            if (bipAddress == null) {
                throw new Exception("BipAddress not found for utxo: "+unspentOutput);
            }
            byte[] privKeyBytes = bipAddress.getHdAddress().getECKey().getPrivKeyBytes();
            add(unspentOutput, privKeyBytes);
        }
    }

    public byte[] getPrivKeyBytes(BipUtxo unspentOutput) {
        String hashKey = hashKey(unspentOutput);
        return privKeys.get(hashKey);
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

    private static String hashKey(BipUtxo unspentOutput) {
        return hashKey(unspentOutput.getTxHash(), unspentOutput.getTxOutputIndex());
    }

    private static String hashKey(String hash, int index) {
        return hash + ":" + index;
    }
}
