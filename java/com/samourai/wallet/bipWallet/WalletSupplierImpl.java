package com.samourai.wallet.bipWallet;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WalletSupplierImpl implements WalletSupplier {
  private static final Logger log = LoggerFactory.getLogger(WalletSupplierImpl.class);

  private IndexHandlerSupplier indexHandlerSupplier;
  private final Map<WhirlpoolAccount, Collection<BipWallet>> walletsByAccount;
  private final Map<String, BipWallet> walletsByPub;
  private final Map<String, BipWallet> walletsById;

  private final Map<WhirlpoolAccount, Map<BipFormat, BipWallet>> walletsByAccountByAddressType; // no custom registrations here

  public WalletSupplierImpl(IndexHandlerSupplier indexHandlerSupplier) {
    this.indexHandlerSupplier = indexHandlerSupplier;

    this.walletsByAccount = new LinkedHashMap<>();
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      walletsByAccount.put(whirlpoolAccount, new LinkedList<>());
    }

    this.walletsByPub = new LinkedHashMap<>();
    this.walletsById = new LinkedHashMap<>();

    this.walletsByAccountByAddressType = new LinkedHashMap<>();
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      walletsByAccountByAddressType.put(whirlpoolAccount, new LinkedHashMap<>());
    }
  }

  public WalletSupplierImpl(IndexHandlerSupplier indexHandlerSupplier, HD_Wallet bip44w) {
    this(indexHandlerSupplier);

    // register Samourai derivations
    for (BIP_WALLET bip : BIP_WALLET.values()) {
      BipWallet bipWallet = new BipWallet(bip44w, indexHandlerSupplier, bip);
      register(bipWallet);
      walletsByAccountByAddressType.get(bip.getAccount()).put(bip.getBipFormat(), bipWallet);
    }
  }

  public void register(BipWallet bipWallet) {
    walletsByAccount.get(bipWallet.getAccount()).add(bipWallet);
    walletsByPub.put(bipWallet.getPub(), bipWallet);
    walletsById.put(bipWallet.getId(), bipWallet);
    // no walletsByAccountByAddressType here
  }

  public void register(String id, HD_Wallet bip44w, WhirlpoolAccount whirlpoolAccount, BipDerivation derivation, BipFormat bipFormat) {
    BipWallet bipWallet = new BipWallet(id, bip44w, indexHandlerSupplier, whirlpoolAccount, derivation, bipFormat);
    register(bipWallet);
  }

  @Override
  public Collection<BipWallet> getWallets() {
    return walletsByPub.values();
  }

  @Override
  public Collection<BipWallet> getWallets(WhirlpoolAccount whirlpoolAccount) {
    return walletsByAccount.get(whirlpoolAccount);
  }

  @Override
  public BipWallet getWalletByPub(String pub) {
    BipWallet bipWallet = walletsByPub.get(pub);
    if (bipWallet == null) {
      log.error("No wallet found for: " + pub);
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
  public String[] getPubs(boolean withIgnoredAccounts, BipFormat... bipFormats) {
    List<String> pubs = new LinkedList<String>();
    for (BipWallet bipWallet : walletsByPub.values()) {
      // filter ignoredAccounts
      if (withIgnoredAccounts || bipWallet.getAccount().isActive()) {
        // filter bipFormats
        if (bipFormats == null || bipFormats.length==0 || ArrayUtils.contains(bipFormats, bipWallet.getBipFormat())) {
          String pub = bipWallet.getPub();
          pubs.add(pub);
        }
      }
    }
    return pubs.toArray(new String[] {});
  }
}
