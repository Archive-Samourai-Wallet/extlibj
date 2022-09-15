package com.samourai.wallet.util;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;

import java.math.BigInteger;

public class CryptoTestUtil {
    private static final HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();
    private CryptoTestUtil() {}
    private static final ECKey ecKey = ECKey.fromPrivate(new BigInteger("45292090369707310635285627500870691371399357286012942906204494584441273561412"));

    private static CryptoTestUtil instance = null;
    public static CryptoTestUtil getInstance() {
        if(instance == null) {
            instance = new CryptoTestUtil();
        }
        return instance;
    }

    public byte[] generateSeed() throws Exception {
        int nbWords = 12;
        // len == 16 (12 words), len == 24 (18 words), len == 32 (24 words)
        int len = (nbWords / 3) * 4;

        byte seed[] = RandomUtil.getInstance().nextBytes(len);
        return seed;
    }

    public HD_Wallet generateWallet(int purpose, NetworkParameters networkParameters) throws Exception {
        byte seed[] = generateSeed();
        return hdWalletFactory.getHD(purpose, seed, "test", networkParameters);
    }

    public BIP47Wallet generateBip47Wallet(NetworkParameters networkParameters) throws Exception {
        HD_Wallet bip44Wallet = generateWallet(44, networkParameters);
        BIP47Wallet bip47Wallet = new BIP47Wallet(bip44Wallet);
        return bip47Wallet;
    }

    public SegwitAddress generateSegwitAddress(NetworkParameters params) {
        SegwitAddress segwitAddress = new SegwitAddress(ecKey, params);
        return segwitAddress;
    }

    public TransactionOutPoint generateTransactionOutPoint(String toAddress, long amount, NetworkParameters params) throws Exception {
        // generate transaction with bitcoinj
        Transaction transaction = new Transaction(params);

        // add output
        TransactionOutput transactionOutput =
            Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, amount, params);
        transaction.addOutput(transactionOutput);

        // add coinbase input
        int txCounter = 1;
        TransactionInput transactionInput =
            new TransactionInput(
                params, transaction, new byte[] {(byte) txCounter, (byte) (txCounter++ >> 8)});
        transaction.addInput(transactionInput);

        TransactionOutPoint transactionOutPoint = transactionOutput.getOutPointFor();
        transactionOutPoint.setValue(Coin.valueOf(amount));
        return transactionOutPoint;
    }

}
