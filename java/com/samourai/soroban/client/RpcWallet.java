package com.samourai.soroban.client;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.Bip47Encrypter;
import com.samourai.wallet.bip47.rpc.Bip47Partner;
import com.samourai.wallet.bip47.rpc.PaymentCode;

public interface RpcWallet {
    BIP47Wallet getBip47Wallet();
    Bip47Encrypter getBip47Encrypter();
    Bip47Partner getBip47Partner(PaymentCode paymentCodePartner, boolean initiator) throws Exception;
    RpcWallet createNewIdentity();
}
