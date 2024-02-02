package com.samourai.soroban.client;

import com.samourai.wallet.bip47.rpc.*;

public interface RpcWallet {
    BIP47Account getBip47Account();
    Bip47Encrypter getBip47Encrypter();
    Bip47Partner getBip47Partner(PaymentCode paymentCodePartner, boolean initiator) throws Exception;
    RpcWallet createNewIdentity();
}
