package com.samourai.wallet.crypto;

import com.samourai.wallet.crypto.impl.ECDHKeySet;
import com.samourai.wallet.crypto.impl.EncryptedMessage;
import org.bitcoinj.core.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CryptoUtilTest {
  private CryptoUtil cryptoUtil = CryptoUtil.getInstance();

  @Test
  public void encryptDecrypt() throws Exception {
    doEncryptDecrypt("encryption test ...", new ECKey(), new ECKey());
  }

  private void doEncryptDecrypt(String data, ECKey keySender, ECKey keyReceiver) throws Exception {
    // encrypt
    ECDHKeySet ecdhKeySet1 = cryptoUtil.getSharedSecret(keySender, keyReceiver);
    byte[] encrypted = cryptoUtil.encrypt(data, ecdhKeySet1);

    // decrypt
    ECDHKeySet ecdhKeySet2 = cryptoUtil.getSharedSecret(keyReceiver, keySender);
    String decrypted = cryptoUtil.decryptString(encrypted, ecdhKeySet2);

    Assertions.assertEquals(data, decrypted);
  }

  @Test
  public void createSignatureVerify() throws Exception {
    doCreateSignatureVerify("signature test ...".getBytes(), new ECKey());
  }

  private void doCreateSignatureVerify(byte[] data, ECKey key) throws Exception {
    // sign
    byte[] signature = cryptoUtil.createSignature(key, data);

    // verify
    Assertions.assertTrue(cryptoUtil.verifySignature(key, data, signature));
    Assertions.assertFalse(cryptoUtil.verifySignature(key, "wrong data".getBytes(), signature));
    Assertions.assertFalse(cryptoUtil.verifySignature(key, data, "wrong signature".getBytes()));
    Assertions.assertFalse(cryptoUtil.verifySignature(new ECKey(), data, signature));
  }
}
