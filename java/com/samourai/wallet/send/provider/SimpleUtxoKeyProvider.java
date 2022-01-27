package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutPoint;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleUtxoKeyProvider implements UtxoKeyProvider {
  private BipFormatSupplier bipFormatSupplier;
  private Map<String, ECKey> keys;

  public SimpleUtxoKeyProvider() {
    this(BIP_FORMAT.PROVIDER);
  }

  public SimpleUtxoKeyProvider(BipFormatSupplier bipFormatSupplier) {
    this.bipFormatSupplier = bipFormatSupplier;
    this.keys = new LinkedHashMap<String, ECKey>();
  }

  public void setKey(TransactionOutPoint outPoint, ECKey key) {
    keys.put(outPoint.toString(), key);
  }

  @Override
  public ECKey _getPrivKey(String utxoHash, int utxoIndex) throws Exception {
    return keys.get(utxoHash + ":" + utxoIndex);
  }

  @Override
  public BipFormatSupplier getBipFormatSupplier() {
    return bipFormatSupplier;
  }
}
