package com.samourai.wallet.hd;

import java.util.List;
import java.util.ArrayList;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.samourai.wallet.segwit.FidelityTimelockAddress;
import com.samourai.wallet.util.Util;

// test vectors taken from https://gist.github.com/chris-belcher/7257763cedcc014de2cd4239857cd36e
public class FidelityTimelocksTest {

    @Test
    public void testVectors()  {

      NetworkParameters params = MainNetParams.get();

        try  {

          HD_WalletFactoryGeneric hdWalletFactory = HD_WalletFactoryGeneric.getInstance();
          byte[] seed = hdWalletFactory.computeSeedFromWords("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about");
          HD_Wallet hdw84 = hdWalletFactory.getBIP84(seed, "", params);

          List<String> pubkeys = new ArrayList<String>();
          List<String> privkeys = new ArrayList<String>();
          List<Integer> indexes = new ArrayList<Integer>();
          List<String> redeemScripts = new ArrayList<String>();
          List<String> scriptPubkeys = new ArrayList<String>();
          List<String> addresses = new ArrayList<String>();

          pubkeys.add("02a1b09f93073c63f205086440898141c0c3c6d24f69a18db608224bcf143fa011");
          privkeys.add("L2tQBEdhC48YLeEWNg3e4msk94iKfyVa9hdfzRwUERabZ53TfH3d");
          indexes.add(0);
          redeemScripts.add("0400e10b5eb1752102a1b09f93073c63f205086440898141c0c3c6d24f69a18db608224bcf143fa011ac");
          scriptPubkeys.add("0020bdee9515359fc9df912318523b4cd22f1c0b5410232dc943be73f9f4f07e39ad");
          addresses.add("bc1qhhhf29f4nlyalyfrrpfrknxj9uwqk4qsyvkujsa7w0ulfur78xkspsqn84");

          pubkeys.add("02599f6db8b33265a44200fef0be79c927398ed0b46c6a82fa6ddaa5be2714002d");
          privkeys.add("KxctaFBzetyc9KXeUr6jxESCZiCEXRuwnQMw7h7hroP6MqnWN6Pf");
          indexes.add(1);
          redeemScripts.add("0480bf345eb1752102599f6db8b33265a44200fef0be79c927398ed0b46c6a82fa6ddaa5be2714002dac");
          scriptPubkeys.add("0020b8f898643991608524ed04e0c6779f632a57f1ffa3a3a306cd81432c5533e9ae");
          addresses.add("bc1qhrufsepej9sg2f8dqnsvvaulvv490u0l5w36xpkds9pjc4fnaxhq7pcm4h");

          pubkeys.add("03ec8067418537bbb52d5d3e64e2868e67635c33cfeadeb9a46199f89ebfaab226");
          privkeys.add("L3SYqae23ZoDDcyEA8rRBK83h1MDqxaDG57imMc9FUx1J8o9anQe");
          indexes.add(240);
          redeemScripts.add("05807eaa8300b1752103ec8067418537bbb52d5d3e64e2868e67635c33cfeadeb9a46199f89ebfaab226ac");
          scriptPubkeys.add("0020e7de0ad2720ae1d6cc9b6ad91af57eb74646762cf594c91c18f6d5e7a873635a");
          addresses.add("bc1qul0q45njptsadnymdtv34at7karyva3v7k2vj8qc7m2702rnvddq0z20u5");

          pubkeys.add("0308c5751121b1ae5c973cdc7071312f6fc10ab864262f0cbd8134f056166e50f3");
          privkeys.add("L5Z9DDMnj5RZMyyPiQLCvN48Xt7GGmev6cjvJXD8uz5EqiY8trNJ");
          indexes.add(959);
          redeemScripts.add("0580785df400b175210308c5751121b1ae5c973cdc7071312f6fc10ab864262f0cbd8134f056166e50f3ac");
          scriptPubkeys.add("0020803268e042008737cf439748cbb5a4449e311da9aa64ae3ac56d84d059654f85");
          addresses.add("bc1qsqex3czzqzrn0n6rjayvhddygj0rz8df4fj2uwk9dkzdqkt9f7zs5c493u");

          for(int i = 0; i < pubkeys.size(); i++) {

            FidelityTimelockAddress faddress = new FidelityTimelockAddress(Util.hexToBytes(pubkeys.get(i)), params, indexes.get(i));

            String redeemScript = Util.bytesToHex(faddress.fidelityBondTimelockRedeemScript().getProgram());
            byte[] scriptpubkey = faddress.fidelityBondTimelockOutputScript().getProgram();
            String saddress = faddress.getFidelityBondTimelockAddressAsString();

            // test vectors
            Assertions.assertEquals(redeemScripts.get(i), Util.bytesToHex(faddress.fidelityBondTimelockRedeemScript().getProgram()).toLowerCase());
            Assertions.assertEquals(scriptPubkeys.get(i), Util.bytesToHex(scriptpubkey).toLowerCase());
            Assertions.assertEquals(addresses.get(i), saddress);

            // path tests
            Assertions.assertEquals(pubkeys.get(i), Util.bytesToHex(hdw84.getAddressAt(0, 2, indexes.get(i)).getECKey().getPubKey()).toLowerCase());
            Assertions.assertEquals(privkeys.get(i), hdw84.getAddressAt(0, 2, indexes.get(i)).getECKey().getPrivateKeyAsWiF(params));
          }

        }
        catch(Exception e)  {
          e.printStackTrace();
        }

    }

}
