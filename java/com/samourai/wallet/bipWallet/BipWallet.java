package com.samourai.wallet.bipWallet;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.client.indexHandler.IIndexHandler;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.hd.*;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BipWallet {
  private static final Logger log = LoggerFactory.getLogger(BipWallet.class);

  private String id;
  private HD_Wallet hdWallet;
  private HD_Account hdAccount;
  private IndexHandlerSupplier indexHandlerSupplier;
  private WhirlpoolAccount whirlpoolAccount;
  private BipDerivation derivation;
  private BipFormat bipFormat;
  private String pub;

  public BipWallet(HD_Wallet bip44w, IndexHandlerSupplier indexHandlerSupplier, BIP_WALLET bip) {
    this(bip.name(), bip44w, indexHandlerSupplier, bip.getAccount(), bip.getBipDerivation(), bip.getBipFormat());
  }

  public BipWallet(String id, HD_Wallet bip44w, IndexHandlerSupplier indexHandlerSupplier, WhirlpoolAccount whirlpoolAccount, BipDerivation derivation, BipFormat bipFormat) {
    this.id = id;
    this.hdWallet = new HD_Wallet(derivation.getPurpose(), bip44w);
    this.hdAccount = this.hdWallet.getAccount(derivation.getAccountIndex());
    this.indexHandlerSupplier = indexHandlerSupplier;
    this.whirlpoolAccount = whirlpoolAccount;
    this.derivation = derivation;
    this.bipFormat = bipFormat;
    this.pub = bipFormat.getPub(hdAccount);
  }

  // address

  public BipAddress getNextAddress(){
    return getNextAddress(true);
  }

  public BipAddress getNextAddress(boolean increment) {
    int nextAddressIndex = increment ? getIndexHandlerReceive().getAndIncrement() : getIndexHandlerReceive().get();
    return getAddressAt(Chain.RECEIVE.getIndex(), nextAddressIndex);
  }

  public BipAddress getNextChangeAddress() {
    return getNextChangeAddress(true);
  }

  public BipAddress getNextChangeAddress(boolean increment) {
    int nextAddressIndex =
            increment ? getIndexHandlerChange().getAndIncrement() : getIndexHandlerChange().get();
    return getAddressAt(Chain.CHANGE.getIndex(), nextAddressIndex);
  }

  public BipAddress getAddressAt(int chainIndex, int addressIndex) {
    HD_Address hdAddress = hdWallet.getAddressAt(derivation.getAccountIndex(), chainIndex, addressIndex);
    return new BipAddress(hdAddress, this);
  }

  public BipAddress getAddressAt(UnspentOutput utxo) {
    if (!utxo.hasPath()) {
      return null; // bip47
    }
    return getAddressAt(utxo.computePathChainIndex(), utxo.computePathAddressIndex());
  }

  //

  public String getId() {
    return id;
  }

  public WhirlpoolAccount getAccount() {
    return whirlpoolAccount;
  }

  public IIndexHandler getIndexHandlerReceive() {
    return getIndexHandler(Chain.RECEIVE);
  }

  public IIndexHandler getIndexHandlerChange() {
    return getIndexHandler(Chain.CHANGE);
  }

  public IIndexHandler getIndexHandler(Chain chain) {
    return indexHandlerSupplier.getIndexHandlerWallet(this, chain);
  }

  public BipDerivation getDerivation() {
    return derivation;
  }

  public BipFormat getBipFormat() {
    return bipFormat;
  }

  public String getPub() {
    return pub;
  }

  public NetworkParameters getParams() {
    return hdWallet.getParams();
  }

  public HD_Wallet getHdWallet() {
    return hdWallet;
  }

  public HD_Account getHdAccount() {
    return hdAccount;
  }
}
