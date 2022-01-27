package com.samourai.wallet.bipFormat;

import com.samourai.wallet.hd.HD_Account;
import com.samourai.wallet.hd.HD_Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;

public interface BipFormat {
  String getId();
  String getLabel();
  String getPub(HD_Account hdAccount);
  String getAddressString(HD_Address hdAddress);
  void sign(Transaction tx, int inputIndex, ECKey key) throws Exception;
}
