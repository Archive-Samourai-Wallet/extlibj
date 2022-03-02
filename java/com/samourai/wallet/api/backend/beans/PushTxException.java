package com.samourai.wallet.api.backend.beans;

public class PushTxException extends Exception {
  private String pushTxError;

  public PushTxException(String pushTxError) {
    super("PushTx failed: "+pushTxError);
    this.pushTxError = pushTxError;
  }

  public String getPushTxError() {
    return pushTxError;
  }
}
