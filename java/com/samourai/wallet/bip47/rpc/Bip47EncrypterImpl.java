package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.bip47.BIP47UtilGeneric;
import com.samourai.wallet.crypto.CryptoUtil;
import com.samourai.wallet.crypto.impl.ECDHKeySet;
import com.samourai.wallet.util.MessageSignUtilGeneric;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bip47EncrypterImpl implements Bip47Encrypter {
  private static final Logger log = LoggerFactory.getLogger(Bip47EncrypterImpl.class);
  private static final MessageSignUtilGeneric messageSignUtil = MessageSignUtilGeneric.getInstance();

  private BIP47Wallet bip47Wallet;
  private CryptoUtil cryptoUtil;
  private BIP47UtilGeneric bip47Util;

  public Bip47EncrypterImpl(
      BIP47Wallet bip47Wallet,
      CryptoUtil cryptoUtil,
      BIP47UtilGeneric bip47Util) {
    this.bip47Wallet = bip47Wallet;
    this.cryptoUtil = cryptoUtil;
    this.bip47Util = bip47Util;
  }

  @Override
  public String sign(String message) throws Exception {
    ECKey notificationAddressKey = bip47Wallet.getNotificationAddress().getECKey();
    return messageSignUtil.signMessage(notificationAddressKey, message);
  }

  @Override
  public boolean verifySignature(String message, String signature, PaymentCode signingPaymentCode) throws Exception {
    NetworkParameters params = bip47Wallet.getParams();
    String signingAddress = signingPaymentCode.notificationAddress(params).getAddressString();
    return messageSignUtil.verifySignedMessage(signingAddress, message, signature, params);
  }

  protected ECDHKeySet getSharedSecret(PaymentCode paymentCodePartner) throws Exception {
    NetworkParameters params = bip47Wallet.getParams();
    ECKey notificationAddressKey = bip47Wallet.getNotificationAddress().getECKey();
    ECKey partnerKey = paymentCodePartner.notificationAddress(params).getECKey();
    return cryptoUtil.getSharedSecret(notificationAddressKey, partnerKey);
  }

  @Override
  public String decrypt(byte[] encrypted, PaymentCode paymentCodePartner) throws Exception {
    ECDHKeySet sharedSecret = getSharedSecret(paymentCodePartner);
    return cryptoUtil.decryptString(encrypted, sharedSecret);
  }

  @Override
  public byte[] encrypt(String payload, PaymentCode paymentCodePartner) throws Exception {
    ECDHKeySet sharedSecret = getSharedSecret(paymentCodePartner);
    return cryptoUtil.encrypt(payload.getBytes(), sharedSecret);
  }

  @Override
  public PaymentAddress getSharedPaymentAddress(PaymentCode paymentCodePartner) throws Exception {
    NetworkParameters params = bip47Wallet.getParams();
    ECKey notificationAddressKey = bip47Wallet.getNotificationAddress().getECKey();
    return bip47Util.getPaymentAddress(paymentCodePartner, 0, notificationAddressKey, params);
  }

  @Override
  public PaymentCode getPaymentCode() {
    return bip47Wallet.getPaymentCode();
  }

  @Override
  public NetworkParameters getParams() {
    return bip47Wallet.getParams();
  }
}
