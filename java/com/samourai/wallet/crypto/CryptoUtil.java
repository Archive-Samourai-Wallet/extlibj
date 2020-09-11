package com.samourai.wallet.crypto;

import com.samourai.wallet.crypto.impl.ECDH;
import com.samourai.wallet.crypto.impl.ECDHKeySet;
import com.samourai.wallet.crypto.impl.EncryptedMessage;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CryptoUtil {
    private static final Logger log = LoggerFactory.getLogger(CryptoUtil.class);

    private static CryptoUtil instance;

    public static CryptoUtil getInstance() {
        if (instance == null) {
            instance = new CryptoUtil();
        }
        return instance;
    }

    public byte[] encrypt (byte[] data, ECDHKeySet keySet) throws Exception {
        byte[] enc = encryptAES_CTR(data, keySet.encryptionKey, keySet.ivServer, keySet.counterOut);
        byte[] hmac = getHMAC(enc, keySet.hmacKey);
        return new EncryptedMessage(hmac, enc).serialize();
    }

    public byte[] encrypt (String data, ECDHKeySet keySet) throws Exception {
        return encrypt(data.getBytes("UTF-8"), keySet);
    }

    public byte[] decrypt (byte[] encrypted, ECDHKeySet ecdhKeySet) throws Exception {
        EncryptedMessage message = EncryptedMessage.unserialize(encrypted);
        checkHMAC(message.hmac, message.payload, ecdhKeySet.hmacKey);
        byte[] data = decryptAES_CTR(message.payload, ecdhKeySet.encryptionKey, ecdhKeySet.ivClient, ecdhKeySet.counterIn);
        return data;
    }

    public String decryptString (byte[] encrypted, ECDHKeySet ecdhKeySet) throws Exception {
        byte[] data = decrypt(encrypted, ecdhKeySet);
        return new String(data, "UTF-8");
    }

    public ECDHKeySet getSharedSecret (ECKey keyServer, ECKey keyClient) {
        return ECDH.getSharedSecret(keyServer, keyClient);
    }

    public byte[] createSignature (ECKey pubkey, byte[] data) throws NoSuchProviderException, NoSuchAlgorithmException {
        return pubkey.sign(Sha256Hash.of(data)).encodeToDER();
    }

    public boolean verifySignature (ECKey pubkey, byte[] data, byte[] signature) throws NoSuchProviderException, NoSuchAlgorithmException {
        try {
            MessageDigest hashHandler = MessageDigest.getInstance("SHA256", "BC");
            hashHandler.update(data);
            byte[] hash = hashHandler.digest();
            return pubkey.verify(hash, signature);
        } catch(Exception e) {
            log.error("", e);
            return false;
        }
    }

    //

    private byte[] decryptAES_CTR (byte[] data, byte[] keyBytes, byte[] ivBytes, long counter) throws Exception {
        byte[] ivWithCounter = new byte[16];
        System.arraycopy(ivBytes, 0, ivWithCounter, 0, ivBytes.length);
        byte[] counterBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(counter).array();
        System.arraycopy(counterBytes, 0, ivWithCounter, ivBytes.length, counterBytes.length);

        //Initialisation
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivWithCounter);

        //Mode
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        return cipher.doFinal(data);
    }

    private byte[] encryptAES_CTR (byte[] data, byte[] keyBytes, byte[] ivBytes, long counter) throws Exception {
        byte[] ivWithCounter = new byte[16];
        System.arraycopy(ivBytes, 0, ivWithCounter, 0, ivBytes.length);
        byte[] counterBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(counter).array();
        System.arraycopy(counterBytes, 0, ivWithCounter, ivBytes.length, counterBytes.length);

        //Initialisation
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivWithCounter);

        //Mode
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(data);
    }

    private byte[] getHMAC (byte[] data, byte[] keyBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        return mac.doFinal(data);
    }

    private void checkHMAC (byte[] hmac, byte[] rest, byte[] keyBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] result = mac.doFinal(rest);


        if (!MessageDigest.isEqual(result, hmac)){
            throw new RuntimeException("HMAC does not match..");
        }
    }
}
