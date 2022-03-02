package com.samourai.wallet.test;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.segwit.SegwitAddress;
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
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

  public AbstractTest() {
    httpClient = new JettyHttpClient(5000, Optional.empty(), "test");
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
