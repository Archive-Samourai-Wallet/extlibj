package com.samourai.wallet.bipFormat;

import org.bitcoinj.core.NetworkParameters;

import java.util.Collection;

public interface BipFormatSupplier {
      BipFormat findByAddress(String address, NetworkParameters params);
      BipFormat findById(String bipFormatId);
      Collection<BipFormat> getList();
  }