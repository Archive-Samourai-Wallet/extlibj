package com.samourai.soroban.client;

import com.samourai.wallet.bip47.rpc.BIP47Account;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import org.bitcoinj.core.ECKey;

public class RpcWalletImpl implements RpcWallet {
    private BIP47Wallet bip47Wallet;

    public RpcWalletImpl(BIP47Wallet bip47Wallet) {
        this.bip47Wallet = bip47Wallet;
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
}
