package com.samourai.wallet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private static final Logger log = LoggerFactory.getLogger(RandomUtil.class);

    private static RandomUtil instance = null;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static boolean testMode = false;

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
        if (testMode) {
            return new byte[length];
        }
        byte b[] = new byte[length];
        secureRandom.nextBytes(b);
        return b;
    }

    public static int random(int minInclusive, int maxInclusive) {
        if (testMode) {
            return minInclusive;
        }
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static long random(long minInclusive, long maxInclusive) {
        if (testMode) {
            return minInclusive;
        }
        return ThreadLocalRandom.current().nextLong(minInclusive, maxInclusive + 1);
    }

    // returns random number between [0, bound-1]
    public int nextInt(int bound) {
        if (testMode) {
            return 0;
        }
        return getSecureRandom().nextInt(bound);
    }

    public long nextLong() {
        if (testMode) {
            return 0;
        }
        return getSecureRandom().nextLong();
    }

    public void shuffle(List list) {
        if (testMode) {
            return;
        }
        Collections.shuffle(list);
    }

    public static void _setTestMode() {
        testMode = true;
    }
}
