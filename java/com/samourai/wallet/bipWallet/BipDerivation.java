package com.samourai.wallet.bipWallet;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.hd.HD_Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BipDerivation {
  private static final Logger log = LoggerFactory.getLogger(BipDerivation.class);

  private int purpose;
  private int accountIndex;

  public BipDerivation(int purpose, int accountIndex) {
    this.purpose = purpose;
    this.accountIndex = accountIndex;
  }

  public String getPathAccount() {
    return HD_Address.getPathAccount(purpose, 0, accountIndex);
  }

  public String getPathChain(int chainIndex) {
    return HD_Address.getPathChain(purpose, 0, accountIndex, chainIndex);
  }

  public String getPathAddress(UnspentOutput utxo) {
    return utxo.getPathAddress(purpose, accountIndex);
  }

  public String getPathAddress(HD_Address hdAddress) {
    return HD_Address.getPathAddress(purpose, 0, accountIndex, hdAddress.getChainIndex(), hdAddress.getAddressIndex());
  }

  public int getPurpose() {
    return purpose;
  }

  public int getAccountIndex() {
    return accountIndex;
  }
}
