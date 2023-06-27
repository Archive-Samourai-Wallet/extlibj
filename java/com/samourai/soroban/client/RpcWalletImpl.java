package com.samourai.soroban.client;

import com.samourai.soroban.client.dialog.Encrypter;
import com.samourai.soroban.client.dialog.PaynymEncrypter;
import com.samourai.wallet.bip47.rpc.BIP47Account;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.crypto.CryptoUtil;
import org.bitcoinj.core.ECKey;

public class RpcWalletImpl implements RpcWallet {
    private BIP47Wallet bip47Wallet;
    private CryptoUtil cryptoUtil;

    public RpcWalletImpl(BIP47Wallet bip47Wallet, CryptoUtil cryptoUtil) {
        this.bip47Wallet = bip47Wallet;
        this.cryptoUtil = cryptoUtil;
    }

    @Override
    public BIP47Wallet getBip47Wallet() {
        return bip47Wallet;
    }

    @Override
    public PaymentCode getPaymentCode() {
        return new PaymentCode(getBip47Account().getPaymentCode());
    }

    @Override
    public ECKey getPaymentCodeKey() {
        return getBip47Account().getNotificationAddress().getECKey();
    }

    @Override
    public BIP47Account getBip47Account() {
        return bip47Wallet.getAccount(0);
    }

    @Override
    public Encrypter getEncrypter() {
        return new PaynymEncrypter(getPaymentCode(),
                getPaymentCodeKey(),
                bip47Wallet.getParams(),
                cryptoUtil);
    }
}
