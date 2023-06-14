package com.samourai.wallet.bipWallet;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.Util;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WalletSupplierImpl implements WalletSupplier {
  private static final Logger log = LoggerFactory.getLogger(WalletSupplierImpl.class);

  private BipFormatSupplier bipFormatSupplier;
  private IndexHandlerSupplier indexHandlerSupplier;
  private final Map<WhirlpoolAccount, Collection<BipWallet>> walletsByAccount;
  private final Map<String, BipWallet> walletsByXPub;
  private final Map<String, BipWallet> walletsById;

  private final Map<WhirlpoolAccount, Map<BipFormat, BipWallet>> walletsByAccountByAddressType; // no custom registrations here

  public WalletSupplierImpl(BipFormatSupplier bipFormatSupplier, IndexHandlerSupplier indexHandlerSupplier) {
    this.bipFormatSupplier = bipFormatSupplier;
    this.indexHandlerSupplier = indexHandlerSupplier;

    this.walletsByAccount = new LinkedHashMap<>();
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      walletsByAccount.put(whirlpoolAccount, new LinkedList<>());
    }

    this.walletsByXPub = new LinkedHashMap<>();
    this.walletsById = new LinkedHashMap<>();

    this.walletsByAccountByAddressType = new LinkedHashMap<>();
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      walletsByAccountByAddressType.put(whirlpoolAccount, new LinkedHashMap<>());
    }
  }

  public WalletSupplierImpl(BipFormatSupplier bipFormatSupplier, IndexHandlerSupplier indexHandlerSupplier, HD_Wallet bip44w) {
    this(bipFormatSupplier, indexHandlerSupplier);

    // register Samourai derivations
    for (BIP_WALLET bip : BIP_WALLET.values()) {
      BipWallet bipWallet = new BipWallet(bipFormatSupplier, bip44w, indexHandlerSupplier, bip);
      register(bipWallet);
    }
  }

  public void register(BipWallet bipWallet) {
    walletsByAccount.get(bipWallet.getAccount()).add(bipWallet);
    walletsByXPub.put(bipWallet.getXPub(), bipWallet);
    walletsById.put(bipWallet.getId(), bipWallet);
    for (BipFormat bipFormat : bipWallet.getBipFormats()) {
      walletsByAccountByAddressType.get(bipWallet.getAccount()).put(bipFormat, bipWallet);
    }
    if (log.isDebugEnabled()) {
      log.debug("+BipWallet["+bipWallet.getId()+"]: "+bipWallet.toString());
    }
    // no walletsByAccountByAddressType here
  }

  public void register(String id, HD_Wallet bip44w, WhirlpoolAccount whirlpoolAccount, BipDerivation derivation, Collection<BipFormat> bipFormats, BipFormat bipFormatDefault) {
    BipWallet bipWallet = new BipWallet(bipFormatSupplier, id, bip44w, indexHandlerSupplier, whirlpoolAccount, derivation, bipFormats, bipFormatDefault);
    register(bipWallet);
  }

  @Override
  public Collection<BipWallet> getWallets() {
    return walletsByXPub.values();
  }

  @Override
  public Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount) {
    return walletsByAccount.get(whirlpoolAccount);
  }

  @Override
  public BipWallet getWalletByXPub(String pub) {
    BipWallet bipWallet = walletsByXPub.get(pub);
    if (bipWallet == null) {
      log.error("BipWallet not found for: " + pub);
      return null;
    }
    return bipWallet;
  }

  @Override
  public BipWallet getWalletById(String id) {
    return walletsById.get(id);
  }

  @Override
  public BipWallet getWallet(BIP_WALLET bip) {
    return getWalletById(bip.name());
  }

  @Override
  public BipWallet getWallet(WhirlpoolAccount account, BipFormat bipFormat) {
    return walletsByAccountByAddressType.get(account).get(bipFormat);
  }

  @Override
  public BipWallet getWallet(UnspentOutput unspentOutput) {
    return getWalletByXPub(unspentOutput.xpub.m);
  }

  @Override
  public BipAddress getAddress(UnspentOutput unspentOutput) {
    BipWallet bipWallet = getWallet(unspentOutput);
    return bipWallet != null ? bipWallet.getAddressAt(unspentOutput) : null;
  }

  @Override
  public String[] getXPubs(boolean withIgnoredAccounts, BipFormat... bipFormats) {
    List<String> xPubs = new LinkedList<>();
    for (BipWallet bipWallet : walletsByXPub.values()) {
      // filter ignoredAccounts
      if (withIgnoredAccounts || bipWallet.getAccount().isActive()) {
        // filter bipFormats
        if (bipFormats == null || bipFormats.length == 0 || !Util.intersection(bipWallet.getBipFormats(), Arrays.asList(bipFormats)).isEmpty()) {
          String xpub = bipWallet.getXPub();
          xPubs.add(xpub);
        }
      }
    }
    return xPubs.toArray(new String[] {});
  }


}
