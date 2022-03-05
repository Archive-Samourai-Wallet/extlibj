package com.samourai.wallet.bipFormat;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;

import java.util.Collection;

public interface BipFormatSupplier {
    Collection<BipFormat> getList();
    BipFormat findByAddress(String address, NetworkParameters params);
    BipFormat findById(String bipFormatId);
    String getToAddress(TransactionOutput output);
    TransactionOutput getTransactionOutput(String address, long amount, NetworkParameters params) throws Exception;
  }