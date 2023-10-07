package com.samourai.wallet.util;

import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.utxo.BipUtxo;
import com.samourai.wallet.utxo.UtxoOutPoint;
import com.samourai.wallet.utxo.UtxoRef;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtxoUtil {
  private static final Logger log = LoggerFactory.getLogger(UtxoUtil.class);
  private static final String PATH_SEPARATOR = "/";

  private static UtxoUtil instance = null;

  public static UtxoUtil getInstance() {
    if(instance == null) {
      instance = new UtxoUtil();
    }
    return instance;
  }

  public TransactionInput computeSpendInput(UtxoOutPoint o, NetworkParameters params) {
    MyTransactionOutPoint myOutPoint = new MyTransactionOutPoint(o, params, 0);
    return new TransactionInput(params, null, new byte[]{}, myOutPoint, Coin.valueOf(o.getValueLong()));
  }

  public String getPathAddress(BipUtxo utxo, int purpose, int accountIndex, NetworkParameters params) {
    int coinType = FormatsUtilGeneric.getInstance().getCoinType(params);
    if (utxo.isBip47()) {
      // bip47
      return HD_Address.getPathAddressBip47(purpose, coinType, accountIndex);
    }
    return HD_Address.getPathAddress(purpose, coinType, accountIndex, utxo.getChainIndex(), utxo.getAddressIndex());
  }

  public String computePath(HD_Address hdAddress) {
    return computePath(hdAddress.getChainIndex(), hdAddress.getAddressIndex());
  }

  public String computePath(BipUtxo bipUtxo) {
    return computePath(bipUtxo.getChainIndex(), bipUtxo.getAddressIndex());
  }

  public String computePath(int chainIndex, int addressIndex) {
    return "m"+ PATH_SEPARATOR+chainIndex+PATH_SEPARATOR+addressIndex;
  }

  public Integer computeConfirmedBlockHeight(int utxoConfirmations, int latestBlockHeight) {
    if (utxoConfirmations <= 0) {
      return null;
    }
    return latestBlockHeight - utxoConfirmations;
  }

  public int computeConfirmations(Integer confirmedBlockHeight, int latestBlockHeight) {
    if (confirmedBlockHeight == null) {
      return 0;
    }
    return latestBlockHeight - confirmedBlockHeight;
  }

  public Integer computeChainIndex(String path) {
    try {
      return Integer.parseInt(path.split(PATH_SEPARATOR)[1]);
    } catch (Exception e) {
      return null;
    }
  }

  public Integer computeAddressIndex(String path) {
    try {
      return Integer.parseInt(path.split(PATH_SEPARATOR)[2]);
    } catch (Exception e) {
      return null;
    }
  }

  public String utxoToKey(UtxoRef utxoRef) {
    return utxoToKey(utxoRef.getTxHash(), utxoRef.getTxOutputIndex());
  }

  public String utxoToKey(TransactionOutPoint outPoint) {
    return utxoToKey(outPoint.getHash().toString(), (int)outPoint.getIndex());
  }

  public String utxoToKey(String utxoHash, int utxoIndex) {
    return utxoHash + ':' + utxoIndex;
  }
}
