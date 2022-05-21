package com.samourai.wallet.cahoots;

import com.samourai.wallet.cahoots.multi.MultiCahoots;

public class CahootsTestUtil {
    public static void cleanPayload(Cahoots copy) {
        // TODO static values for test
        copy.strID = "testID";
        copy.ts = 123456;
        copy.psbt = null;

        if (copy instanceof MultiCahoots) {
            MultiCahoots multiCahoots = (MultiCahoots)copy;
            cleanPayload(multiCahoots.getStonewallx2());
            cleanPayload(multiCahoots.getStowaway());
        }
    }
}
