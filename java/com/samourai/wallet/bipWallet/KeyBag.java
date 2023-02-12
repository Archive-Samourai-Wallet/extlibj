package com.samourai.wallet.bipWallet;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Address;
import org.bitcoinj.core.ECKey;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeyBag {
    private final Map<String,byte[]> privKeys;

    public KeyBag() {
        this.privKeys = new LinkedHashMap<>();
    }

    public void add(UnspentOutput unspentOutput, byte[] privKeyBytes) {
        String hashKey = hashKey(unspentOutput);
        this.privKeys.put(hashKey, privKeyBytes);
    }

    public void add(UnspentOutput unspentOutput, WalletSupplier walletSupplier) throws Exception {
        BipAddress bipAddress = walletSupplier.getAddress(unspentOutput);
        if (bipAddress == null) {
            throw new Exception("BipAddress not found for utxo: "+unspentOutput);
        }
        byte[] privKeyBytes = bipAddress.getHdAddress().getECKey().getPrivKeyBytes();
        add(unspentOutput, privKeyBytes);
    }

    public void addAll(Collection<UnspentOutput> unspentOutputs, WalletSupplier walletSupplier) throws Exception {
        for (UnspentOutput unspentOutput : unspentOutputs) {
            BipAddress bipAddress = walletSupplier.getAddress(unspentOutput);
            if (bipAddress == null) {
                throw new Exception("BipAddress not found for utxo: "+unspentOutput);
            }
            byte[] privKeyBytes = bipAddress.getHdAddress().getECKey().getPrivKeyBytes();
            add(unspentOutput, privKeyBytes);
        }
    }

    public byte[] getPrivKeyBytes(UnspentOutput unspentOutput) {
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

    private static String hashKey(UnspentOutput unspentOutput) {
        return hashKey(unspentOutput.tx_hash, unspentOutput.tx_output_n);
    }

    private static String hashKey(String hash, int index) {
        return hash + ":" + index;
    }
}
