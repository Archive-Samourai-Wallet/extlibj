package com.samourai.wallet.util;

import com.samourai.wallet.bip69.BIP69InputComparator;
import com.samourai.wallet.bip69.BIP69OutputComparator;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.utxo.InputOutPoint;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TxUtil {
  private static final Logger log = LoggerFactory.getLogger(TxUtil.class);

  private static TxUtil instance = null;

  public static TxUtil getInstance() {
    if(instance == null) {
      instance = new TxUtil();
    }
    return instance;
  }

  public void verifySignInput(Transaction tx, int inputIdx, long inputValue, byte[] connectedScriptBytes) throws Exception {
    Script connectedScript = new Script(connectedScriptBytes);
    tx.getInput(inputIdx).getScriptSig().correctlySpends(tx, inputIdx, connectedScript, Coin.valueOf(inputValue), Script.ALL_VERIFY_FLAGS);
  }

  public Integer findInputIndex(Transaction tx, String txoHash, long txoIndex) {
    for (int i = 0; i < tx.getInputs().size(); i++) {
      TransactionInput input = tx.getInput(i);
      TransactionOutPoint outPoint = input.getOutpoint();
      if (outPoint.getHash().toString().equals(txoHash) && outPoint.getIndex() == txoIndex) {
        return i;
      }
    }
    return null;
  }

  public byte[] findInputPubkey(Transaction tx, int inputIndex, Callback<byte[]> fetchInputOutpointScriptBytes) {
    TransactionInput transactionInput = tx.getInput(inputIndex);
    if (transactionInput == null) {
      return null;
    }

    // try P2WPKH / P2SH-P2WPKH: get from witness
    byte[] inputPubkey = null;
    try {
      inputPubkey = tx.getWitness(inputIndex).getPush(1);
      if (inputPubkey != null) {
        return inputPubkey;
      }
    } catch(Exception e) {
      // witness not found
    }

    // try P2PKH: get from input script
    Script inputScript = new Script(transactionInput.getScriptBytes());
    try {
      inputPubkey = inputScript.getPubKey();
      if (inputPubkey != null) {
        return inputPubkey;
      }
    } catch(Exception e) {
      // not P2PKH
    }

    // try P2PKH: get pubkey from input script
    if (fetchInputOutpointScriptBytes != null) {
      byte[] inputOutpointScriptBytes = fetchInputOutpointScriptBytes.execute();
      if (inputOutpointScriptBytes != null) {
        inputPubkey = new Script(inputOutpointScriptBytes).getPubKey();
      }
    }
    return inputPubkey;
  }

  public TransactionOutput findOutputByAddress(Transaction tx, String address, BipFormatSupplier bipFormatSupplier) throws Exception {
    TransactionOutput txOut = null;
    for (TransactionOutput transactionOutput : tx.getOutputs()) {
      String toAddress = bipFormatSupplier.getToAddress(transactionOutput);
      if(toAddress.equalsIgnoreCase(address)) {
        txOut = transactionOutput;
        break;
      }
    }
    return txOut;
  }

  public String getTxHex(Transaction tx) {
    return org.bitcoinj.core.Utils.HEX.encode(tx.bitcoinSerialize());
  }

  public Transaction fromTxHex(NetworkParameters params, String txHex) {
    return new Transaction(params, org.bitcoinj.core.Utils.HEX.decode(txHex));
  }

  public void sortBip69Inputs(Transaction transaction) {
    List<TransactionInput> inputs = new ArrayList<TransactionInput>();
    inputs.addAll(transaction.getInputs());
    Collections.sort(inputs, new BIP69InputComparator());
    replaceInputs(transaction, inputs);
  }

  public void sortBip69Outputs(Transaction transaction) {
    List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
    outputs.addAll(transaction.getOutputs());
    Collections.sort(outputs, new BIP69OutputComparator());
    replaceOutputs(transaction, outputs);
  }

  public void sortBip69InputsAndOutputs(Transaction transaction) {
    sortBip69Inputs(transaction);
    sortBip69Outputs(transaction);
  }

  public boolean isBip69Sorted(Transaction transaction) {
    String raw = getTxHex(transaction);

    // sort
    Transaction bip69Tx = fromTxHex(transaction.getParams(), raw);
    TxUtil.getInstance().sortBip69InputsAndOutputs(bip69Tx);

    // compare
    return getTxHex(bip69Tx).equals(raw);
  }

  public void replaceInputs(Transaction transaction, List<TransactionInput> allInputs) {
    transaction.clearInputs();
    for (TransactionInput txIn : allInputs) {
      transaction.addInput(txIn);
    }
  }

  public void replaceOutputs(Transaction transaction, List<TransactionOutput> allOutputs) {
    transaction.clearOutputs();
    for (TransactionOutput txOut : allOutputs) {
      transaction.addOutput(txOut);
    }
  }

  public long computeSpendAmount(Transaction tx, KeyBag keyBag, Collection<String> outputAddresses, BipFormatSupplier bipFormatSupplier, Function<TransactionOutPoint, InputOutPoint> getInputOutPoint) throws Exception {
    long spendAmount = 0;

    for(TransactionInput input : tx.getInputs()) {
      if (keyBag.getPrivKeyBytes(input.getOutpoint()) != null) {
        // read inputValue from provided InputOutPoint
        long value = getInputOutPoint.apply(input.getOutpoint()).getValueLong();
        if (log.isDebugEnabled()) {
          log.debug("computeSpendAmount... +input " + value + " " + input);
        }
        spendAmount += value;
      }
    }

    for(TransactionOutput output : tx.getOutputs()) {
      if (!output.getScriptPubKey().isOpReturn()) {
        String outputAddress = bipFormatSupplier.getToAddress(output);
        if (outputAddress != null && outputAddresses.contains(outputAddress)) {
          if (output.getValue() != null) {
            if (log.isDebugEnabled()) {
              log.debug("computeSpendAmount... -output " + output.getValue().longValue() + " " + outputAddress);
            }
            spendAmount -= output.getValue().longValue();
          }
        }
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("computeSpendAmount = " + spendAmount);
    }
    return spendAmount;
  }
}
