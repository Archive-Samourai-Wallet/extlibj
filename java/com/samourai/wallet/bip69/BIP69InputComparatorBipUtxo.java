package com.samourai.wallet.bip69;

import com.samourai.wallet.utxo.BipUtxo;

public class BIP69InputComparatorBipUtxo extends BIP69InputComparatorGeneric<BipUtxo> {
  @Override
  protected long getIndex(BipUtxo i) {
    return i.getTxOutputIndex();
  }

  @Override
  protected byte[] getHash(BipUtxo i) {
    return i.getTxHash().getBytes();
  }
}
