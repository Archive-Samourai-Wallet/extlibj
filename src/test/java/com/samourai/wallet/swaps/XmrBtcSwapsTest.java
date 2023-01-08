package com.samourai.wallet.hd.swaps;

import java.util.List;
import java.util.ArrayList;
import java.security.SignatureException;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.crypto.TransactionSignature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.samourai.wallet.segwit.bech32.Bech32Segwit;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.segwit.SwapSupportAddress;
import com.samourai.wallet.util.Util;

public class XmrBtcSwapsTest {

  /*
  Example cancel transaction:
  http://mempoolhqx4isw62xs7abwphsq7ldayuidyx2v2oethdhhj6mlo2r6ad.onion/testnet/tx/f24ea8dd633c3d77a75257efc528c17634e99565f1041020015464342d864881
  Witness data: <sig> <sig> <redeemScript>
  Redeem script: <33 byte push> OP_CHECKSIGVERIFY <33 byte push> OP_CHECKSIG
  Input sequence: 0x0c
  */
    @Test
    public void cancelTransactionFromPubkey() throws Exception {

      NetworkParameters params = TestNet3Params.get();

      SwapSupportAddress saddress = new SwapSupportAddress(Util.hexToBytes("02d424445b480ca374c6c8c6588a5d98d004e941b0d9908b36ea2b6f6c082b76aa"), Util.hexToBytes("0273dc97956e5e99692a1f00d7c509fe06b0b8a2cee2ed60fc7cb73b42698468fa"), params);
      Script redeemScript = saddress.redeemScript();

      String prevHash = "b8409a463be0c8595a48a08d1d76ec4be72bf9be71a274912e4992e5c65095c5";
      long prevIdx = 0;
      long prevAmount = 999875L;
      long spendAmount = 998875L;

      String toAddress = "tb1qs463yssmxs5l35ru496mhxw3llgfdgf87jzpnezzvqzl9axxn97sa59f4l";

      Transaction tx = new Transaction(params);
      tx.setVersion(2);
      tx.setLockTime(0L);

      TransactionOutPoint outpoint = new TransactionOutPoint(params, prevIdx, Sha256Hash.wrap(prevHash));
      TransactionInput input = new TransactionInput(params, null, new byte[0], outpoint);
      input.setSequenceNumber(0x0c);
      TransactionOutput output = Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, spendAmount, params);
      tx.addInput(input);
      tx.addOutput(output);

      TransactionWitness witness = new TransactionWitness(3);
      witness.setPush(0, Util.hexToBytes("3045022100dead2c8fc622948a20afa53c650f5c0b34f8ff31652e1bf6bd764e5bc885cecf022003dc9183c683e4ba980101f05e783f9cbef740ffaa3d96d91426028d3fd9ef5601"));
      witness.setPush(1, Util.hexToBytes("3045022100ca5971e078c0c2976348abb63d01ca2f41dbc09eec8248cf9cba4f44ada97af70220687badce4bcfb0a42a8ba877252a2ed928cadca8b97cedc4d13a6fb85e36a63901"));
      witness.setPush(2, saddress.redeemScript().getProgram());
      tx.setWitness(0, witness);
/*
      byte[] serialized = tx.bitcoinSerialize();
      Assertions.assertEquals(Util.bytesToHex(serialized).toLowerCase(), "02000000000101c59550c6e592492e9174a271bef92be74bec761d8da0485a59c8e03b469a40b800000000000c00000001db3d0f0000000000220020857512421b3429f8d07ca975bb99d1ffd096a127f48419e4426005f2f4c6997d03483045022100dead2c8fc622948a20afa53c650f5c0b34f8ff31652e1bf6bd764e5bc885cecf022003dc9183c683e4ba980101f05e783f9cbef740ffaa3d96d91426028d3fd9ef5601483045022100ca5971e078c0c2976348abb63d01ca2f41dbc09eec8248cf9cba4f44ada97af70220687badce4bcfb0a42a8ba877252a2ed928cadca8b97cedc4d13a6fb85e36a63901462102d424445b480ca374c6c8c6588a5d98d004e941b0d9908b36ea2b6f6c082b76aaad210273dc97956e5e99692a1f00d7c509fe06b0b8a2cee2ed60fc7cb73b42698468faac00000000");
*/
      Assertions.assertEquals("f24ea8dd633c3d77a75257efc528c17634e99565f1041020015464342d864881", tx.getHash().toString());

    }

    /*
    Example refund transaction:
    http://mempoolhqx4isw62xs7abwphsq7ldayuidyx2v2oethdhhj6mlo2r6ad.onion/testnet/tx/d433a0e9ca59a6151898982d1252a6cf02f2f4738f98847b87acdfbcaab1c3d6
    Witness data: <sig> <sig> <redeemScript>
    Redeem script: <33 byte push> OP_CHECKSIGVERIFY <33 byte push> OP_CHECKSIG
    Input sequence: 0xffffffff
    */
      @Test
      public void refundTransactionFromPubkey() throws Exception  {

        NetworkParameters params = TestNet3Params.get();

        SwapSupportAddress saddress = new SwapSupportAddress(Util.hexToBytes("02d424445b480ca374c6c8c6588a5d98d004e941b0d9908b36ea2b6f6c082b76aa"), Util.hexToBytes("0273dc97956e5e99692a1f00d7c509fe06b0b8a2cee2ed60fc7cb73b42698468fa"), params);
        Script redeemScript = saddress.redeemScript();

        String prevHash = "f24ea8dd633c3d77a75257efc528c17634e99565f1041020015464342d864881";
        long prevIdx = 0;
        long prevAmount = 998875L;
        long spendAmount = 997875L;

        String toAddress = "tb1qgnr8vl6ejy9jjxrlfkecn5pkeprd27mj0ggn3d";

        Transaction tx = new Transaction(params);
        tx.setVersion(2);
        tx.setLockTime(0L);

        TransactionOutPoint outpoint = new TransactionOutPoint(params, prevIdx, Sha256Hash.wrap(prevHash));
        TransactionInput input = new TransactionInput(params, null, new byte[0], outpoint);
        input.setSequenceNumber(0xffffffff);
        TransactionOutput output = Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, spendAmount, params);
        tx.addInput(input);
        tx.addOutput(output);

        TransactionWitness witness = new TransactionWitness(3);
        witness.setPush(0, Util.hexToBytes("3044022019ebc5f361a4b3856f4c22bc8fedce47b7940513997890c636c3ad334472a8ac0220084743421bcc5346c8973b59ffd927ab9068b030a00abba6cd87a12da81b87fa01"));
        witness.setPush(1, Util.hexToBytes("304502210082325b9c893485c1c015c657d76e6ecb53292ad3b4d11e55589440fa1fe835b9022046ecee326af66189f580564a4978fd31393b45f6d7fba302259176feaaab7c5d01"));
        witness.setPush(2, saddress.redeemScript().getProgram());
        tx.setWitness(0, witness);

        Assertions.assertEquals("d433a0e9ca59a6151898982d1252a6cf02f2f4738f98847b87acdfbcaab1c3d6", tx.getHash().toString());

      }

      // other cancel txs
      // 94c449166de6f1f5ef262de7b0caeaa50569214dbff6fba75b9eaeb903ce70a9, cTscEfx3sbZCQJ5M3ZdoNxpreDVzk9VVxX2Nxz1tJv2WxoGJJEUS, cQmpiPcCn9adLMspLoLXgFQYXRMhhZAUuXti5X5CNeGWw7H3JhVy
      // 1f819ad3b38737752d1914050d80d15aa3d00d0c2a64a3e64213906b72bdf818, cUVrBBnGVjVJgP8kLLgsoAvtzJGWFXzk8gTLxBwSLWa9Y3rj1sNn, cN48VXu3nDsMesUWNoZhF5KsbxRpbuAQrYgC6xkEbmBjdL8oWpTd
      // 1e6b07ae4781f85f2441f94a8026b4fdde3509d0dca14001f51d5e9057253e9a, cSdzxBCNGiy87xh533V6fnpGkZ5GBMAYqTm5umGi381oMvV5QjAh, cMssEFwH9DH4VjEsaKPBpe9WvueLZQRFoAtFCudazkRVagxgu15A
      @Test
      public void cancelTransactionFromPrivkey() throws Exception  {

        NetworkParameters params = TestNet3Params.get();

        DumpedPrivateKey dpk0 = DumpedPrivateKey.fromBase58(params, "cVqCRn1m8YxUwrmYu4jSZ2Zz3z4KM6gx8bCz9Fo5WLwmeJKprR4a");
        ECKey eckey0 = dpk0.getKey();
        DumpedPrivateKey dpk1 = DumpedPrivateKey.fromBase58(params, "cMt9m9iswJMYfHAUKPGnmsHsBR7HRcbEVUfSJje7E7tDjRT22kac");
        ECKey eckey1 = dpk1.getKey();

        SwapSupportAddress saddress = new SwapSupportAddress(eckey0, eckey1, params);
        Script redeemScript = saddress.redeemScript();
        Assertions.assertEquals("tb1qcq8udrke9htlyqnpfyugfe38jkp8c0kla9r5x2ghkj5nmuv0gvssasedhd", saddress.getDefaultToAddressAsString());

        String prevHash = "836761d2754c95f761f37eaf57823ca97e611507bb1569b41517b087e681161a";
        long prevIdx = 2;
        long prevAmount = 125000L;
        long spendAmount = 124200L;

        String toAddress = "tb1ptrsvc2pd4det6lyluakgmhkdehmy92nuyg7ddhul3kjvc2tlkj6sv78gps";

        Transaction tx = new Transaction(params);
        tx.setVersion(2);
        tx.setLockTime(0L);

        TransactionOutPoint outpoint = new TransactionOutPoint(params, prevIdx, Sha256Hash.wrap(prevHash));
        TransactionInput input = new TransactionInput(params, null, new byte[0], outpoint);
        input.setSequenceNumber(0x0c);
        TransactionOutput output = Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, spendAmount, params);
        tx.addInput(input);
        tx.addOutput(output);

        TransactionWitness witness = new TransactionWitness(3);
        TransactionSignature sig0 = tx.calculateWitnessSignature(0, eckey1, redeemScript.getProgram(), Coin.valueOf(prevAmount), Transaction.SigHash.ALL, false);
        TransactionSignature sig1 = tx.calculateWitnessSignature(0, eckey0, redeemScript.getProgram(), Coin.valueOf(prevAmount), Transaction.SigHash.ALL, false);
        witness.setPush(0, sig0.encodeToBitcoin());
        witness.setPush(1, sig1.encodeToBitcoin());
        witness.setPush(2, saddress.redeemScript().getProgram());
        tx.setWitness(0, witness);

        Assertions.assertEquals("3179d039ad5681a05de82aa8d29326fe43e98ac0db1bae1ef5e587f8990f0421", tx.getHash().toString());

      }

      // other refund toHexStrin
      // 91740c1b1e11c932091950b96915dd66f49200c48068bd90a935212667c736bf, cW1TyRHfM55toyKbeSiKwuwbsLbbFGncPfs2kecux4e4Yot8Wm9V, cPhohmnV7KbB1WXWiwMRGSAJ9msLD7anmQMTTv2usUJh9gBkKzWQ
      @Test
      public void refundTransactionFromPrivkey() throws Exception  {

        NetworkParameters params = TestNet3Params.get();

        DumpedPrivateKey dpk0 = DumpedPrivateKey.fromBase58(params, "cNvPFKxm663nEQNbkuPkspfJAJL46WtJQyk3qUAZre56YFg7DZ8v");
        ECKey eckey0 = dpk0.getKey();
        DumpedPrivateKey dpk1 = DumpedPrivateKey.fromBase58(params, "cRiaw2WzQKFovdXfVKArFi5hpWK3LmJFd8mb6Q9izHsyWssTZFvC");
        ECKey eckey1 = dpk1.getKey();

        SwapSupportAddress saddress = new SwapSupportAddress(eckey0, eckey1, params);
        Script redeemScript = saddress.redeemScript();
        Assertions.assertEquals("tb1qpz64pqp4n6sdkwfajw3xwg3dyn5sy0c0cu7s2cm8yfvujtrju5zq4tn286", saddress.getDefaultToAddressAsString());

        String prevHash = "0b0eba6ae4511a7cbeed1aafac4613e9d24b9ac232a0d3ebffef99507ebb2512";
        long prevIdx = 1;
        long prevAmount = 125000L;
        long spendAmount = 124200L;

        String toAddress = "tb1pzqsyxqcxmncdv7wlld29s60rlgnjuctyzagdeej7w8yd5wjap8ushwckhd";

        Transaction tx = new Transaction(params);
        tx.setVersion(2);
        tx.setLockTime(0L);

        TransactionOutPoint outpoint = new TransactionOutPoint(params, prevIdx, Sha256Hash.wrap(prevHash));
        TransactionInput input = new TransactionInput(params, null, new byte[0], outpoint);
        input.setSequenceNumber(0xffffffff);
        TransactionOutput output = Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, spendAmount, params);
        tx.addInput(input);
        tx.addOutput(output);

        TransactionWitness witness = new TransactionWitness(3);
        TransactionSignature sig0 = tx.calculateWitnessSignature(0, eckey1, redeemScript.getProgram(), Coin.valueOf(prevAmount), Transaction.SigHash.ALL, false);
        TransactionSignature sig1 = tx.calculateWitnessSignature(0, eckey0, redeemScript.getProgram(), Coin.valueOf(prevAmount), Transaction.SigHash.ALL, false);
        witness.setPush(0, sig0.encodeToBitcoin());
        witness.setPush(1, sig1.encodeToBitcoin());
        witness.setPush(2, saddress.redeemScript().getProgram());
        tx.setWitness(0, witness);

        Assertions.assertEquals("c26e9ec0876da98f69fe11cea8be2b25c866d0a068e4137f44d9f7b4e6bc133f", tx.getHash().toString());

      }

}
