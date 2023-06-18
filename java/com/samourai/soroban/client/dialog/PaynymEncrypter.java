package com.samourai.soroban.client.dialog;

import com.samourai.soroban.client.RpcWallet;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.crypto.CryptoUtil;
import com.samourai.wallet.crypto.impl.ECDHKeySet;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaynymEncrypter implements Encrypter {
  private static final Logger log = LoggerFactory.getLogger(com.samourai.soroban.client.dialog.PaynymEncrypter.class);

  private PaymentCode paymentCode;
  private ECKey paymentCodeKey;
  private NetworkParameters params;
  private CryptoUtil cryptoUtil;

  public PaynymEncrypter(
      PaymentCode paymentCode,
      ECKey paymentCodeKey,
      NetworkParameters params,
      CryptoUtil cryptoUtil) {
    this.paymentCode = paymentCode;
    this.paymentCodeKey = paymentCodeKey;
    this.params = params;
    this.cryptoUtil = cryptoUtil;
  }

  public PaynymEncrypter(RpcWallet rpcWallet, CryptoUtil cryptoUtil) {
    this(
        rpcWallet.getPaymentCode(),
        rpcWallet.getPaymentCodeKey(),
        rpcWallet.getBip47Wallet().getParams(),
        cryptoUtil);
  }

  @Override
  public String decrypt(byte[] encrypted, PaymentCode paymentCodePartner) throws Exception {
    ECKey partnerKey = getPartnerKey(paymentCodePartner);
    ECDHKeySet sharedSecret = cryptoUtil.getSharedSecret(paymentCodeKey, partnerKey);
    return cryptoUtil.decryptString(encrypted, sharedSecret);
  }

  @Override
  public byte[] encrypt(String payload, PaymentCode paymentCodePartner) throws Exception {
    ECKey partnerKey = getPartnerKey(paymentCodePartner);
    ECDHKeySet sharedSecret = cryptoUtil.getSharedSecret(paymentCodeKey, partnerKey);
    return cryptoUtil.encrypt(payload.getBytes(), sharedSecret);
  }

  protected ECKey getPartnerKey(PaymentCode paymentCodePartner) {
    return paymentCodePartner.notificationAddress(params).getECKey();
  }

  @Override
  public PaymentCode getPaymentCode() {
    return paymentCode;
  }
}