package com.samourai.wallet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class RandomUtil {
    private static final Logger log = LoggerFactory.getLogger(TxUtil.class);

    private static RandomUtil instance = null;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static RandomUtil getInstance() {
        if(instance == null) {
            instance = new RandomUtil();
        }
        return instance;
    }

    public static SecureRandom getSecureRandom() {
        return secureRandom;
    }

    //

    public byte[] nextBytes(int length) {
        byte b[] = new byte[length];
        secureRandom.nextBytes(b);
        return b;
    }

}
