package com.samourai.wallet.bip47.rpc;

public interface Bip47Partner {
  BIP47Wallet getBip47Wallet();
  String sign(String message);
  boolean verifySignature(String message, String signature);

  String decrypt(byte[] encrypted) throws Exception;
  byte[] encrypt(String payload) throws Exception;

  String getSharedAddressBech32();
  PaymentCode getPaymentCodePartner();

  Bip47Partner createNewIdentity(BIP47Wallet bip47WalletNewIdentity) throws Exception;
}
