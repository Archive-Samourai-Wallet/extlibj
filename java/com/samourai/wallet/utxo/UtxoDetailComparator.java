package com.samourai.wallet.utxo;

import java.util.Comparator;

public class UtxoDetailComparator implements Comparator<UtxoDetail> {
  public UtxoDetailComparator() {}

  public int compare(UtxoDetail o1, UtxoDetail o2) {
    return o1.getValue() - o2.getValue() > 0L ? 1 : -1;
  }
}
