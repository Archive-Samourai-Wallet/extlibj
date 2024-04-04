package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.crypto.impl.ECDHKeySet;
import com.samourai.wallet.util.ExtLibJConfig;
import com.samourai.wallet.util.MessageSignUtilGeneric;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bip47EncrypterImpl implements Bip47Encrypter {
  private static final Logger log = LoggerFactory.getLogger(Bip47EncrypterImpl.class);
  private static final MessageSignUtilGeneric messageSignUtil = MessageSignUtilGeneric.getInstance();
  private ExtLibJConfig extLibJConfig;

  private BIP47Account bip47Account;

  public Bip47EncrypterImpl(
          ExtLibJConfig extLibJConfig,
      BIP47Account bip47Account) {
    this.extLibJConfig = extLibJConfig;
    this.bip47Account = bip47Account;
  }

  @Override
  public String sign(String message) throws Exception {
    ECKey notificationAddressKey = getSigningKey();
    return messageSignUtil.signMessage(notificationAddressKey, message);
  }

  @Override
  public ECKey getSigningKey() throws Exception {
    return bip47Account.getNotificationAddress().getECKey();
  }

  @Override
  public boolean verifySignature(String message, String signature, PaymentCode signingPaymentCode) throws Exception {
    NetworkParameters params = bip47Account.getParams();
    String signingAddress = signingPaymentCode.notificationAddress(params).getAddressString();
    return messageSignUtil.verifySignedMessage(signingAddress, message, signature, params);
  }

  @Override
  public String getSigningAddress(PaymentCode signingPaymentCode) throws Exception {
    NetworkParameters params = getParams();
    return signingPaymentCode.notificationAddress(params).getAddressString();
  }

  protected ECDHKeySet getSharedSecret(PaymentCode paymentCodePartner) throws Exception {
    NetworkParameters params = bip47Account.getParams();
    ECKey notificationAddressKey = bip47Account.getNotificationAddress().getECKey();
    ECKey partnerKey = paymentCodePartner.notificationAddress(params).getECKey();
    return extLibJConfig.getCryptoUtil().getSharedSecret(notificationAddressKey, partnerKey);
  }

  @Override
  public String decrypt(byte[] encrypted, PaymentCode paymentCodePartner) throws Exception {
    ECDHKeySet sharedSecret = getSharedSecret(paymentCodePartner);
    return extLibJConfig.getCryptoUtil().decryptString(encrypted, sharedSecret);
  }

  @Override
  public byte[] encrypt(String payload, PaymentCode paymentCodePartner) throws Exception {
    ECDHKeySet sharedSecret = getSharedSecret(paymentCodePartner);
    return extLibJConfig.getCryptoUtil().encrypt(payload.getBytes(), sharedSecret);
  }

  @Override
  public PaymentAddress getSharedPaymentAddress(PaymentCode paymentCodePartner) throws Exception {
    NetworkParameters params = bip47Account.getParams();
    ECKey notificationAddressKey = bip47Account.getNotificationAddress().getECKey();
    return extLibJConfig.getBip47Util().getPaymentAddress(paymentCodePartner, 0, notificationAddressKey, params);
  }

  @Override
  public PaymentCode getPaymentCode() {
    return bip47Account.getPaymentCode();
  }

  @Override
  public NetworkParameters getParams() {
    return bip47Account.getParams();
  }
}
