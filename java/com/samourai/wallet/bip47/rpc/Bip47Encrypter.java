package com.samourai.wallet.bip47.rpc;

public interface Bip47Encrypter {

  String sign(String message) throws Exception;
  boolean verifySignature(String message, String signature, PaymentCode signingPaymentCode) throws Exception;

  String decrypt(byte[] payload, PaymentCode paymentCodePartner) throws Exception;
  byte[] encrypt(String payload, PaymentCode paymentCodePartner) throws Exception;

  PaymentAddress getSharedPaymentAddress(PaymentCode paymentCodePartner) throws Exception;
  PaymentCode getPaymentCode();
}
