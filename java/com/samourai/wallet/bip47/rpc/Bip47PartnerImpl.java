package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.crypto.impl.ECDHKeySet;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.util.ExtLibJConfig;
import com.samourai.wallet.util.MessageSignUtilGeneric;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bip47PartnerImpl implements Bip47Partner {
  private static final Logger log = LoggerFactory.getLogger(Bip47PartnerImpl.class);
  private static final MessageSignUtilGeneric messageSignUtil = MessageSignUtilGeneric.getInstance();

  private ExtLibJConfig extLibJConfig;
  private BIP47Account bip47Account;
  private PaymentCode paymentCodePartner;
  private boolean initiator; // true if my role is initiator, false if partner is initiator
  private HD_Address notificationAddressMine;
  private HD_Address notificationAddressPartner;
  private ECDHKeySet sharedSecret;
  private String sharedAddressBech32;

  public Bip47PartnerImpl(
          ExtLibJConfig extLibJConfig,
          BIP47Account bip47Account,
          PaymentCode paymentCodePartner,
          boolean initiator) throws Exception {
    this.extLibJConfig = extLibJConfig;
    this.bip47Account = bip47Account;
    this.paymentCodePartner = paymentCodePartner;
    this.initiator = initiator;

    // precompute these values and throw on invalid paymentcode
    NetworkParameters params = bip47Account.getParams();
    this.notificationAddressMine = bip47Account.getNotificationAddress();
    this.notificationAddressPartner = paymentCodePartner.notificationAddress(params);
    this.sharedSecret = extLibJConfig.getCryptoUtil().getSharedSecret(notificationAddressMine.getECKey(), notificationAddressPartner.getECKey());
    PaymentAddress sharedPaymentAddress = extLibJConfig.getBip47Util().getPaymentAddress(paymentCodePartner, 0, notificationAddressMine.getECKey(), params);
    this.sharedAddressBech32 = sharedPaymentAddress.getSegwitAddress(initiator).getBech32AsString();
  }

  @Override
  public Bip47Partner createNewIdentity(BIP47Account bip47AccountNewIdentity) throws Exception {
    return new Bip47PartnerImpl(extLibJConfig, bip47AccountNewIdentity, paymentCodePartner, initiator);
  }

  @Override
  public String sign(String message) {
    ECKey notificationAddressKey = notificationAddressMine.getECKey();
    return messageSignUtil.signMessage(notificationAddressKey, message);
  }

  @Override
  public boolean verifySignature(String message, String signature) {
    NetworkParameters params = bip47Account.getParams();
    String signingAddress = notificationAddressPartner.getAddressString();
    return messageSignUtil.verifySignedMessage(signingAddress, message, signature, params);
  }

  @Override
  public String decrypt(byte[] encrypted) throws Exception {
    return extLibJConfig.getCryptoUtil().decryptString(encrypted, sharedSecret);
  }

  @Override
  public byte[] encrypt(String payload) throws Exception {
    return extLibJConfig.getCryptoUtil().encrypt(payload.getBytes(), sharedSecret);
  }

  @Override
  public BIP47Account getBip47Account() {
    return bip47Account;
  }

  @Override
  public PaymentCode getPaymentCodePartner() {
    return paymentCodePartner;
  }

  @Override
  public String getSharedAddressBech32() {
    return sharedAddressBech32;
  }

  protected ECDHKeySet getSharedSecret() {
    return sharedSecret;
  }
}
