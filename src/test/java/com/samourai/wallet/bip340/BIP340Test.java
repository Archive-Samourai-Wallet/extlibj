package com.samourai.wallet.bip340;

import java.math.BigInteger;
import java.util.Arrays;

import com.samourai.wallet.segwit.bech32.*;
import com.samourai.wallet.util.Util;

import org.bitcoinj.core.ECKey;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.ECPoint;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BIP340Test {

  // test vectors: https://github.com/bitcoin/bips/blob/master/bip-0086.mediawiki
  private String[] PUBKEYS = {
    "03cc8a4bc64d897bddc5fbc2f670f7a8ba0b386779106cf1223c6fc5d7cd6fc115",
    "0283dfe85a3151d2517290da461fe2815591ef69f2b18a2ce63f01697a8b313145",
    "02399f1b2f4393f29a18c937859c5dd8a77350103157eb880f02e8c08214277cef",
    };

  @Test
  public void deriveP2TRAddress() throws Exception {

    //
    // https://github.com/bitcoin/bips/blob/master/bip-0086.mediawiki
    //

    ECKey ecKey = ECKey.fromPublicOnly(Hex.decode("03cc8a4bc64d897bddc5fbc2f670f7a8ba0b386779106cf1223c6fc5d7cd6fc115"));
    Point iPoint = BIP340Util.getInternalPubkey(ecKey);
    assert(Hex.toHexString(iPoint.toBytes()).equals("cc8a4bc64d897bddc5fbc2f670f7a8ba0b386779106cf1223c6fc5d7cd6fc115"));

    Point oPoint = BIP340Util.getOutputPubkey(iPoint);
    assert(Point.isSecp256k1(oPoint.toBytes()));
    assert(Hex.toHexString(oPoint.toBytes()).equals("a60869f0dbcf1dc659c9cecbaf8050135ea9e8cdc487053f1dc6880949dc684c"));

    String address = BIP340Util.getP2TRAdress(MainNetParams.get(), oPoint);
    assert(address.equals("bc1p5cyxnuxmeuwuvkwfem96lqzszd02n6xdcjrs20cac6yqjjwudpxqkedrcr"));

    ecKey = ECKey.fromPublicOnly(Hex.decode("0283dfe85a3151d2517290da461fe2815591ef69f2b18a2ce63f01697a8b313145"));
    iPoint = BIP340Util.getInternalPubkey(ecKey);
    assert(Hex.toHexString(iPoint.toBytes()).equals("83dfe85a3151d2517290da461fe2815591ef69f2b18a2ce63f01697a8b313145"));

    oPoint = BIP340Util.getOutputPubkey(iPoint);
    assert(Point.isSecp256k1(oPoint.toBytes()));
    assert(Hex.toHexString(oPoint.toBytes()).equals("a82f29944d65b86ae6b5e5cc75e294ead6c59391a1edc5e016e3498c67fc7bbb"));

    address = BIP340Util.getP2TRAdress(MainNetParams.get(), oPoint);
    assert(address.equals("bc1p4qhjn9zdvkux4e44uhx8tc55attvtyu358kutcqkudyccelu0was9fqzwh"));

    ecKey = ECKey.fromPublicOnly(Hex.decode("02399f1b2f4393f29a18c937859c5dd8a77350103157eb880f02e8c08214277cef"));
    iPoint = BIP340Util.getInternalPubkey(ecKey);
    assert(Hex.toHexString(iPoint.toBytes()).equals("399f1b2f4393f29a18c937859c5dd8a77350103157eb880f02e8c08214277cef"));

    oPoint = BIP340Util.getOutputPubkey(iPoint);
    assert(Point.isSecp256k1(oPoint.toBytes()));
    assert(Hex.toHexString(oPoint.toBytes()).equals("882d74e5d0572d5a816cef0041a96b6c1de832f6f9676d9605c44d5e9a97d3dc"));

    address = BIP340Util.getP2TRAdress(MainNetParams.get(), oPoint);
    assert(address.equals("bc1p3qkhfews2uk44qtvauqyr2ttdsw7svhkl9nkm9s9c3x4ax5h60wqwruhk7"));

  }

  @Test
  public void invalidPubKey() throws Exception {

    //
    // https://suredbits.com/taproot-funds-burned-on-the-bitcoin-blockchain/
    //
    assert(!Point.isSecp256k1(Hex.decode("658204033e46a1fa8cceb84013cfe2d376ca72d5f595319497b95b08aa64a970")));

  }

}
