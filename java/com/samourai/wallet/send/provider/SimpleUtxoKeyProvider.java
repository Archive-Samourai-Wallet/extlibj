package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.BipUtxo;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutPoint;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleUtxoKeyProvider implements UtxoKeyProvider {
  private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
  private BipFormatSupplier bipFormatSupplier;
  private Map<String, ECKey> keys;

  public SimpleUtxoKeyProvider() {
    this(BIP_FORMAT.PROVIDER);
  }

  public SimpleUtxoKeyProvider(BipFormatSupplier bipFormatSupplier) {
    this.bipFormatSupplier = bipFormatSupplier;
    this.keys = new LinkedHashMap<>();
  }

  public void setKey(TransactionOutPoint outPoint, ECKey key) {
    String index = utxoUtil.utxoToKey(outPoint);
    keys.put(index, key);
  }

  @Override
  public byte[] _getPrivKey(BipUtxo bipUtxo) throws Exception {
    String index = utxoUtil.utxoToKey(bipUtxo);
    ECKey ecKey = keys.get(index);
    if (ecKey == null) {
      throw new Exception("Key not found for utxo: "+bipUtxo);
    }
    return ecKey.getPrivKeyBytes();
  }

  @Override
  public BipFormatSupplier getBipFormatSupplier() {
    return bipFormatSupplier;
  }
}
