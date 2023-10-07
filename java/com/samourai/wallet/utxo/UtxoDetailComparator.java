package com.samourai.wallet.utxo;

import java.util.Comparator;

/**
 * sort in ascending order by value
 */
public class UtxoDetailComparator implements Comparator<UtxoDetail> {
  public UtxoDetailComparator() {}

  public int compare(UtxoDetail o1, UtxoDetail o2) {
    return o1.getValueLong() - o2.getValueLong() > 0L ? 1 : -1;
  }
}
