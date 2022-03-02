package com.samourai.wallet.util;

import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxUtil {
  private static final Logger log = LoggerFactory.getLogger(TxUtil.class);

  private static TxUtil instance = null;

  private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

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

  public String getToAddress(TransactionOutput output) {
    String outputScript = Hex.toHexString(output.getScriptBytes());
    if (bech32Util.isBech32Script(outputScript)) {
      // bech32
      try {
        String outputAddress = bech32Util.getAddressFromScript(outputScript, output.getParams());
        return outputAddress;
      } catch (Exception e) {
        log.error("", e);
      }
    } else {
      // P2PKH or P2SH
      String outputAddress = output.getScriptPubKey().getToAddress(output.getParams()).toString();
      return outputAddress;
    }
    return null;
  }

  public TransactionOutput computeTransactionOutput(String address, long amount, NetworkParameters params) throws Exception {
    if(!FormatsUtilGeneric.getInstance().isValidBitcoinAddress(address, params) && FormatsUtilGeneric.getInstance().isValidBIP47OpReturn(address)) {
      // BIP47
      Script toOutputScript = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data(Hex.decode(address)).build();
      return new TransactionOutput(params, null, Coin.valueOf(0L), toOutputScript.getProgram());
    }
    else {
      if (FormatsUtilGeneric.getInstance().isValidBech32(address)) {
        // bech32
        return Bech32UtilGeneric.getInstance().getTransactionOutput(address, amount, params);
      } else {
        Script outputScript = ScriptBuilder.createOutputScript(org.bitcoinj.core.Address.fromBase58(params, address));
        return new TransactionOutput(params, null, Coin.valueOf(amount), outputScript.getProgram());
      }
    }
  }

  public String getTxHex(Transaction tx) {
    return org.bitcoinj.core.Utils.HEX.encode(tx.bitcoinSerialize());
  }

  public Transaction fromTxHex(NetworkParameters params, String txHex) {
    return new Transaction(params, org.bitcoinj.core.Utils.HEX.decode(txHex));
  }
}
