package com.samourai.wallet.hd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum WALLET_INDEX {

  BIP44_RECEIVE(BIP_WALLET.DEPOSIT_BIP44, Chain.RECEIVE),
  BIP44_CHANGE(BIP_WALLET.DEPOSIT_BIP44, Chain.CHANGE),

  BIP49_RECEIVE(BIP_WALLET.DEPOSIT_BIP49, Chain.RECEIVE),
  BIP49_CHANGE(BIP_WALLET.DEPOSIT_BIP49, Chain.CHANGE),

  BIP84_RECEIVE(BIP_WALLET.DEPOSIT_BIP84, Chain.RECEIVE),
  BIP84_CHANGE(BIP_WALLET.DEPOSIT_BIP84, Chain.CHANGE),

  PREMIX_RECEIVE(BIP_WALLET.PREMIX_BIP84, Chain.RECEIVE),
  PREMIX_CHANGE(BIP_WALLET.PREMIX_BIP84, Chain.CHANGE),

  POSTMIX_RECEIVE(BIP_WALLET.POSTMIX_BIP84, Chain.RECEIVE),
  POSTMIX_CHANGE(BIP_WALLET.POSTMIX_BIP84, Chain.CHANGE),

  BADBANK_RECEIVE(BIP_WALLET.BADBANK_BIP84, Chain.RECEIVE),
  BADBANK_CHANGE(BIP_WALLET.BADBANK_BIP84, Chain.CHANGE);

  private static final Logger log = LoggerFactory.getLogger(WALLET_INDEX.class);
  private BIP_WALLET bipWallet;
  private Chain chain;

  WALLET_INDEX(BIP_WALLET bipWallet, Chain chain) {
    this.bipWallet = bipWallet;
    this.chain = chain;
  }

  public BIP_WALLET getBipWallet() {
    return bipWallet;
  }

  public Chain getChain() {
    return chain;
  }

  public int getChainIndex() {
    return chain.getIndex();
  }
}
