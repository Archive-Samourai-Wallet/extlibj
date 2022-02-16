package com.samourai.wallet.payload;

import com.samourai.wallet.crypto.AESUtil;
import com.samourai.wallet.util.CharSequenceX;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayloadUtilGeneric {
    private static final Logger log = LoggerFactory.getLogger(PayloadUtilGeneric.class);
    private static PayloadUtilGeneric instance = null;

    protected PayloadUtilGeneric() { ; }

    public static PayloadUtilGeneric getInstance() {
        if(instance == null) {
            instance = new PayloadUtilGeneric();
        }
        return instance;
    }

    public boolean isBackupFile(String data) {
        try {
            JSONObject jsonObj = new JSONObject(data);
            if (jsonObj != null && jsonObj.has("payload")) {
                return true;
            }
        } catch (Exception e) {
            log.error("Not a backup file: "+e.getMessage());
        }
        return false;
    }

    public String decryptBackupFile(String backupFile, String passwordStr) throws Exception {
        String encrypted = null;
        int version = 1;
        try {
            JSONObject jsonObj = new JSONObject(backupFile);
            if(jsonObj != null && jsonObj.has("payload"))    {
                encrypted = jsonObj.getString("payload");
            }
            if(jsonObj != null && jsonObj.has("version"))    {
                version = jsonObj.getInt("version");
            }
        }
        catch(Exception e) {}

        // not a json stream, assume v0
        if(encrypted == null)    {
            encrypted = backupFile;
        }

        // decrypt
        String decrypted = null;
        if (passwordStr == null) {
            decrypted = encrypted;
        } else {
            try {
                CharSequenceX password = new CharSequenceX(passwordStr);
                if (version == 1) {
                    decrypted = AESUtil.decrypt(encrypted, password, AESUtil.DefaultPBKDF2Iterations);
                } else if (version == 2) {
                    decrypted = AESUtil.decryptSHA256(encrypted, password);
                }
            } catch (Exception e) {}
        }
        if (decrypted == null || decrypted.length() < 1) {
            throw new Exception("Unable to decrypt");
        }
        return decrypted;
    }

}