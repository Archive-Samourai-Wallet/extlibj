package com.samourai.wallet.send.provider;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.Chain;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.*;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * utxo provider reserved for tests
 */
public class MockUtxoProvider extends SimpleUtxoKeyProvider implements UtxoProvider {
  private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
  public static final int CONFIRMATIONS_DEFAUT = 999;

  private NetworkParameters params;
  private Map<WhirlpoolAccount, List<UTXO>> utxosByAccount;
  private WalletSupplier walletSupplier;
  private CahootsUtxoProvider cahootsUtxoProvider;
  private int nbUtxos = 0;

  public MockUtxoProvider(NetworkParameters params, WalletSupplier walletSupplier) {
    this.params = params;
    this.walletSupplier = walletSupplier;
    this.cahootsUtxoProvider = new SimpleCahootsUtxoProvider(this);
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

  public UTXO addUtxo(int account, String txid, int n, long value, String address) throws Exception {
    return addUtxo(account, txid, n, value, address, CONFIRMATIONS_DEFAUT);
  }

  public UTXO addUtxo(int account, String txid, int n, long value, String address, int confirmations) throws Exception {
    WhirlpoolAccount whirlpoolAccount = SamouraiAccountIndex.find(account);
    BipWallet bipWallet = walletSupplier.getWallet(whirlpoolAccount, BIP_FORMAT.SEGWIT_NATIVE);
    return addUtxo(bipWallet, Sha256Hash.of(txid.getBytes()).toString(), n, value, address, ECKey.fromPrivate(BigInteger.valueOf(1234)), confirmations, null);
  }

  public UTXO addUtxo(BipWallet bipWallet, long value) throws Exception {
    int n = nbUtxos+1; // keep backward-compatibility with existing tests
    BipFormat bipFormat = bipWallet.getBipFormatDefault();
    BipAddress bipAddress = bipWallet.getAddressAt(0, n, bipFormat);
    String address = bipAddress.getAddressString();
    String path = utxoUtil.computePath(bipAddress.getHdAddress());
    ECKey ecKey = bipAddress.getHdAddress().getECKey();
    // use a namespace specific to BipWallet for generating txid
    int walletUniqueId = new BigInteger(bipWallet.getHdAccount().xpubstr().getBytes()).intValue();
    String txid = generateTxHash(Math.abs(n*walletUniqueId), params);
    return addUtxo(bipWallet, txid, n, value, address, ecKey, CONFIRMATIONS_DEFAUT, path);
  }

  public UTXO addUtxo(BipWallet bipWallet, String txid, int n, long value, String address, ECKey ecKey, int confirmations) throws Exception {
    return addUtxo(bipWallet, txid, n, value, address, ecKey, confirmations,null);
  }

  public UTXO addUtxo(BipWallet bipWallet, String txid, int n, long value, String address, ECKey ecKey, int confirmations, String path) throws Exception {
    String xpub = bipWallet.getXPub();
    UnspentOutput unspentOutput = computeUtxo(txid, n, path, xpub, address, value, confirmations, getBipFormatSupplier(), params);
    MyTransactionOutPoint outPoint = new MyTransactionOutPoint(unspentOutput, params);
    UTXO utxo = new UTXO(Arrays.asList(outPoint), path, xpub);
    nbUtxos++;

    WhirlpoolAccount account = bipWallet.getAccount();
    utxosByAccount.get(account).add(utxo);
    setKey(outPoint, ecKey);
    return utxo;
  }

  private static UnspentOutput computeUtxo(String hash, int n, String path, String xpub, String address, long value, int confirms, BipFormatSupplier bipFormatSupplier, NetworkParameters params) throws Exception {
    UnspentOutput utxo = new UnspentOutput();
    utxo.tx_hash = hash;
    utxo.tx_output_n = n;
    utxo.xpub = new UnspentOutput.Xpub();
    utxo.xpub.path = path;
    utxo.xpub.m = xpub;
    utxo.confirmations = confirms;
    utxo.addr = address;
    utxo.value = value;
    utxo.script = Hex.toHexString(bipFormatSupplier.getTransactionOutput(address, value, params).getScriptBytes());
    return utxo;
  }

  private static String generateTxHash(int uniqueId, NetworkParameters params) {
    Transaction tx = new Transaction(params);
    tx.addOutput(Coin.valueOf(uniqueId), ECKey.fromPrivate(BigInteger.valueOf(uniqueId))); // mock key)
    return tx.getHashAsString();
  }

  @Override
  public String getNextAddressChange(WhirlpoolAccount account, BipFormat bipFormat, boolean increment) {
    BipWallet bipWallet = walletSupplier.getWallet(account, bipFormat);
    return bipWallet.getNextAddressChange(increment).getAddressString();
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
    }).collect(Collectors.toList());
  }

  public CahootsUtxoProvider getCahootsUtxoProvider() {
    return cahootsUtxoProvider;
  }

  public WalletSupplier getWalletSupplier() {
    return walletSupplier;
  }
}
