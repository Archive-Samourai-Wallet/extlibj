package com.samourai.wallet.send.provider;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.Chain;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.TestUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleUtxoProvider extends SimpleUtxoKeyProvider implements UtxoProvider {

  private NetworkParameters params;
  private Map<WhirlpoolAccount, List<UTXO>> utxosByAccount;
  private WalletSupplierImpl walletSupplier;
  private int nbUtxos = 0;

  public SimpleUtxoProvider(NetworkParameters params, WalletSupplierImpl walletSupplier) {
    this.params = params;
    this.walletSupplier = walletSupplier;
    utxosByAccount = new LinkedHashMap<>();

    // init wallets
    for (WhirlpoolAccount account : WhirlpoolAccount.values()) {
      utxosByAccount.put(account, new LinkedList<>());
    }
  }

  public void clear() {
    // reset indexs
    for (WhirlpoolAccount whirlpoolAccount : WhirlpoolAccount.values()) {
      Collection<BipWallet> bipWallets = walletSupplier.getWallets(whirlpoolAccount);
      for (BipWallet bipWallet : bipWallets) {
        for (Chain chain : Chain.values()) {
          bipWallet.getIndexHandler(chain).set(0, true);
        }
      }
    }

    // clear utxos
    nbUtxos=0;
    for (List<UTXO> utxos : utxosByAccount.values()) {
      utxos.clear();
    }
  }

  public UTXO addUtxo(BipWallet bipWallet, long value) throws Exception {
    UTXO utxo = new UTXO();

    nbUtxos++;
    BipAddress bipAddress = bipWallet.getAddressAt(0, nbUtxos);
    String address = bipAddress.getAddressString();
    String pub = bipWallet.getPub();
    UnspentOutput unspentOutput = TestUtil.computeUtxo(TestUtil.generateTxHash(nbUtxos), nbUtxos, pub, address, value, 999);
    MyTransactionOutPoint outPoint = unspentOutput.computeOutpoint(params);
    utxo.getOutpoints().add(outPoint);
    WhirlpoolAccount account = bipWallet.getAccount();
    utxosByAccount.get(account).add(utxo);
    setKey(outPoint, bipAddress.getHdAddress().getECKey());
    return utxo;
  }

  @Override
  public String getChangeAddress(WhirlpoolAccount account, BipFormat bipFormat) {
    BipWallet bipWallet = walletSupplier.getWallet(account, bipFormat);
    return bipWallet.getNextChangeAddress().getAddressString();
  }

  @Override
  public Collection<UTXO> getUtxos(WhirlpoolAccount account) {
    return utxosByAccount.get(account);
  }

  @Override
  public Collection<UTXO> getUtxos(WhirlpoolAccount account, BipFormat bipFormat) {
    return utxosByAccount.get(account).stream().filter(utxo -> {
      // TODO zeroleak optimize
      String address = utxo.getOutpoints().iterator().next().getAddress();
      return getBipFormatSupplier().findByAddress(address, params)==bipFormat;
    }).collect(Collectors.<UTXO>toList());
  }
}
