package com.samourai.wallet.util;

import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.utxo.BipUtxo;
import com.samourai.wallet.utxo.UtxoDetail;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

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

  public MyTransactionOutPoint computeOutpoint(BipUtxo utxo, NetworkParameters params) {
    return computeOutpoint(utxo, utxo.getScriptBytes(), params);
  }

  public MyTransactionOutPoint computeOutpoint(UtxoDetail utxo, byte[] scriptBytes, NetworkParameters params) {
    Sha256Hash sha256Hash = Sha256Hash.wrap(Hex.decode(utxo.getTxHash()));
    // use MyTransactionOutPoint to forward scriptBytes + address
    return new MyTransactionOutPoint(params, sha256Hash, utxo.getTxOutputIndex(), BigInteger.valueOf(utxo.getValue()), scriptBytes, utxo.getAddress(), 0);
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
}
