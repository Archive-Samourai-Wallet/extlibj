package com.samourai.wallet.send.provider;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.client.BipWallet;
import com.samourai.wallet.client.indexHandler.IIndexHandler;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandler;
import com.samourai.wallet.hd.AddressType;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.TestUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.bitcoinj.core.NetworkParameters;

import java.util.*;

public class SimpleUtxoProvider extends SimpleUtxoKeyProvider implements UtxoProvider {

  private NetworkParameters params;
  private Map<WhirlpoolAccount, Map<AddressType,BipWallet>> walletsByAccount;
  private Map<WhirlpoolAccount, List<UTXO>> utxosByAccount;
  private IIndexHandler indexHandler = new MemoryIndexHandler();
  private IIndexHandler indexChangeHandler = new MemoryIndexHandler();
  private int nbUtxos = 0;

  public SimpleUtxoProvider(HD_Wallet hdWallet) {
    params = hdWallet.getParams();
    walletsByAccount = new LinkedHashMap<>();
    utxosByAccount = new LinkedHashMap<>();
    for (WhirlpoolAccount account : WhirlpoolAccount.values()) {
      // init wallets
      Map<AddressType,BipWallet> wallets = new LinkedHashMap<>();
      for (AddressType addressType : account.getAddressTypes()) {
        BipWallet bipWallet = new BipWallet(hdWallet, account, indexHandler, indexChangeHandler, addressType);
        wallets.put(addressType, bipWallet);
      }
      walletsByAccount.put(account, wallets);

      // init utxos
      utxosByAccount.put(account, new LinkedList<>());
    }
  }

  public void clear() {
    indexHandler.set(0, true);
    indexChangeHandler.set(0, true);

    // clear utxos
    nbUtxos=0;
    for (List<UTXO> utxos : utxosByAccount.values()) {
      utxos.clear();
    }

    // reset indexs
    for (WhirlpoolAccount account : WhirlpoolAccount.values()) {
      for (AddressType addressType : account.getAddressTypes()) {
        walletsByAccount.get(account).get(addressType).getIndexHandler().set(0, true);
        walletsByAccount.get(account).get(addressType).getIndexChangeHandler().set(0, true);
      }
    }
  }

  public UTXO addUtxo(WhirlpoolAccount account, AddressType addressType, long value) throws Exception {
    UTXO utxo = new UTXO();

    nbUtxos++;
    BipWallet bipWallet = walletsByAccount.get(account).get(addressType);
    String pub = bipWallet.getPub(addressType);
    HD_Address hdAddress = bipWallet.getAddressAt(0, nbUtxos);
    String address = hdAddress.getAddressString(addressType);
    UnspentOutput unspentOutput = TestUtil.computeUtxo(TestUtil.generateTxHash(nbUtxos), nbUtxos, pub, address, value, 999);
    MyTransactionOutPoint outPoint = unspentOutput.computeOutpoint(params);
    utxo.getOutpoints().add(outPoint);
    utxosByAccount.get(account).add(utxo);
    setKey(outPoint, hdAddress.getECKey());
    return utxo;
  }

  @Override
  public String getChangeAddress(WhirlpoolAccount account, AddressType addressType) {
    return walletsByAccount.get(account).get(addressType).getNextChangeAddress().getAddressString(addressType);
  }

  @Override
  public Collection<UTXO> getUtxos(WhirlpoolAccount account) {
    return utxosByAccount.get(account);
  }

  @Override
  public Collection<UTXO> getUtxos(WhirlpoolAccount account, AddressType addressType) {
    return StreamSupport.stream(utxosByAccount.get(account)).filter(new Predicate<UTXO>() {
      @Override
      public boolean test(UTXO utxo) {
        return AddressType.findByAddress(utxo.getOutpoints().iterator().next().getAddress(), params)==addressType;
      }
    }).collect(Collectors.<UTXO>toList());
  }
}
