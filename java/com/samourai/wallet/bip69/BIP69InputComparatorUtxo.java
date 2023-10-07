package com.samourai.wallet.bip69;

import com.samourai.wallet.utxo.UtxoRef;

public class BIP69InputComparatorUtxo extends BIP69InputComparatorGeneric<UtxoRef> {
  @Override
  protected long getIndex(UtxoRef i) {
    return i.getTxOutputIndex();
  }

  @Override
  protected byte[] getHash(UtxoRef i) {
    return i.getTxHash().getBytes();
  }
}
