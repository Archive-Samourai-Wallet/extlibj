package com.samourai.wallet.cahoots;

public class CahootsTestUtil {
    public static void cleanPayload(Cahoots copy) {
        // TODO static values for test
        copy.strID = "testID";
        copy.ts = 123456;
        copy.psbt = null;
    }
}
