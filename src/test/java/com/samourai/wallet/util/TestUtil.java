package com.samourai.wallet.util;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.send.SendFactoryGeneric;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

public class TestUtil {
    private static final NetworkParameters params = TestNet3Params.get();
    private static final HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();

    public static HD_Wallet computeBip44wallet(String seedWords, String passphrase) throws Exception {
        byte[] seed = hdWalletFactory.computeSeedFromWords(seedWords);
        HD_Wallet bip84w = hdWalletFactory.getBIP44(seed, passphrase, params);
        return bip84w;
    }

    public static HD_Wallet computeBip84wallet(String seedWords, String passphrase) throws Exception {
        byte[] seed = hdWalletFactory.computeSeedFromWords(seedWords);
        HD_Wallet bip84w = hdWalletFactory.getBIP84(seed, passphrase, params);
        return bip84w;
    }

    public static UnspentOutput computeUtxo(String hash, int n, String xpub, String address, long value, int confirms) throws Exception {
        UnspentOutput utxo = new UnspentOutput();
        utxo.tx_hash = hash;
        utxo.tx_output_n = n;
        utxo.xpub = new UnspentOutput.Xpub();
        utxo.xpub.m = xpub;
        utxo.confirmations = confirms;
        utxo.addr = address;
        utxo.value = value;
        utxo.script = Hex.toHexString(SendFactoryGeneric.getInstance().computeTransactionOutput(address, value, params).getScriptBytes()); // TODO ?
        return utxo;
    }

    public static String generateTxHash(int i) {
        Transaction tx = new Transaction(params);
        long uniqueId = i*1000;
        tx.addOutput(Coin.valueOf(uniqueId), ECKey.fromPrivate(BigInteger.valueOf(uniqueId))); // generate unique hash
        return tx.getHashAsString();
    }
}
