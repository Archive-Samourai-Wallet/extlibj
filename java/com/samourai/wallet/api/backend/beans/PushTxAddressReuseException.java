package com.samourai.wallet.api.backend.beans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PushTxAddressReuseException extends PushTxException {
  private List<Integer> adressReuseOutputIndexs;

  public PushTxAddressReuseException(Collection<Integer> adressReuseOutputIndexs) {
    super("Address reuse for outputs "+adressReuseOutputIndexs);
    this.adressReuseOutputIndexs = new LinkedList<>(adressReuseOutputIndexs);
  }

  public List<Integer> getAdressReuseOutputIndexs() {
    return adressReuseOutputIndexs;
  }
}
