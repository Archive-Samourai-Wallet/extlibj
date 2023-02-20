package com.samourai.soroban.client;

import com.samourai.wallet.bip47.rpc.BIP47Account;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import org.bitcoinj.core.ECKey;

public interface RpcWallet {
    BIP47Wallet getBip47Wallet();
    BIP47Account getBip47Account();
    PaymentCode getPaymentCode();
    ECKey getPaymentCodeKey();
}
