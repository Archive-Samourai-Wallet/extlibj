package com.samourai.wallet.test;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.wallet.api.backend.BackendApi;
import com.samourai.wallet.api.backend.BackendServer;
import com.samourai.wallet.bip47.BIP47UtilGeneric;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.java.Bip47UtilJava;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.payload.PayloadUtilGeneric;
import com.samourai.wallet.send.provider.SimpleUtxoProvider;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.xmanager.client.XManagerClient;
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AbstractTest {
  protected static final Logger log = LoggerFactory.getLogger(AbstractTest.class);

  protected static final String SEED_WORDS = "all all all all all all all all all all all all";
  protected static final String SEED_PASSPHRASE = "whirlpool";

  protected NetworkParameters params = TestNet3Params.get();
  protected HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();
  protected IHttpClient httpClient;
  protected BipFormatSupplier bipFormatSupplier = BIP_FORMAT.PROVIDER;
  protected PayloadUtilGeneric payloadUtil = PayloadUtilGeneric.getInstance();
  protected WalletSupplierImpl walletSupplier;
  protected HD_Wallet bip44w;
  protected BIP47Wallet bip47Wallet;
  protected BackendApi backendApi;
  protected SimpleUtxoProvider utxoProvider;
  protected XManagerClient xManagerClient;
  protected BIP47UtilGeneric bip47Util = Bip47UtilJava.getInstance();

  public AbstractTest() {
    try {
      setUp();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setUp() throws Exception {
    httpClient = new JettyHttpClient(10000, Optional.empty(), "test");

    byte[] seed = hdWalletFactory.computeSeedFromWords(SEED_WORDS);
    bip44w = hdWalletFactory.getBIP44(seed, SEED_PASSPHRASE, params);
    bip47Wallet = new BIP47Wallet(bip44w);
    walletSupplier = new WalletSupplierImpl(new MemoryIndexHandlerSupplier(), bip44w);
    utxoProvider = new SimpleUtxoProvider(bip44w.getParams(), walletSupplier);
    xManagerClient = new XManagerClient(httpClient, true, false);

    backendApi = computeBackendApi(params);
  }

  protected BackendApi computeBackendApi(NetworkParameters params) {
    boolean testnet = FormatsUtilGeneric.getInstance().isTestNet(params);
    return BackendApi.newBackendApiSamourai(httpClient, BackendServer.get(testnet).getBackendUrl(false));
  }

  protected Transaction computeTxCoinbase(long value, Script outputScript) {
    Transaction tx = new Transaction(params);

    // add output
    tx.addOutput(Coin.valueOf(value), outputScript);

    // add input: coinbase
    int txCounter = 1;
    TransactionInput input =
            new TransactionInput(
                    params, tx, new byte[] {(byte) txCounter, (byte) (txCounter++ >> 8)});
    tx.addInput(input);

    tx.verify();
    return tx;
  }

  protected Transaction computeSpendTx(TransactionOutput txOutput) {
    // spend coinbase
    TransactionOutPoint inputOutPoint = txOutput.getOutPointFor();
    inputOutPoint.setValue(txOutput.getValue());

    Transaction tx = new Transaction(params);

    // add input
    TransactionInput txInput = new TransactionInput(params, null, new byte[0], inputOutPoint, inputOutPoint.getValue());
    tx.addInput(txInput);
    return tx;
  }
}
