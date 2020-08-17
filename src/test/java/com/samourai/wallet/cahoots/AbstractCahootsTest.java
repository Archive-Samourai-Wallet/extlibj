package com.samourai.wallet.cahoots;

import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.java.HD_WalletFactoryJava;
import com.samourai.wallet.segwit.BIP84Wallet;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsTest.class);

    protected HD_WalletFactoryJava hdWalletFactory = HD_WalletFactoryJava.getInstance();
    protected NetworkParameters params = TestNet3Params.get();

    protected BIP84Wallet computeBip84wallet(String seedWords, String passphrase) throws Exception {
        byte[] seed = hdWalletFactory.computeSeedFromWords(seedWords);
        HD_Wallet bip84w = hdWalletFactory.getBIP84(seed, passphrase, params);
        BIP84Wallet bip84Wallet = new BIP84Wallet(bip84w, params);
        return bip84Wallet;
    }

    protected Stowaway cleanPayload(Stowaway payload) {
        Stowaway copy = new Stowaway(payload);
        doCleanPayload(copy);
        return copy;
    }

    protected STONEWALLx2 cleanPayload(STONEWALLx2 payload) {
        STONEWALLx2 copy = new STONEWALLx2(payload);
        doCleanPayload(copy);
        return copy;
    }

    private void doCleanPayload(Cahoots copy) {
        // TODO static values for test
        copy.strID = "testID";
        copy.ts = 123456;
        copy.psbt = null;
    }

    protected void verify(String expectedPayload, STONEWALLx2 payload) {
        String payloadStr = cleanPayload(payload).toJSONString();
        log.info("### payload="+payloadStr);
        Assertions.assertEquals(expectedPayload, payloadStr);
    }

    protected void verify(String expectedPayload, Stowaway payload) {
        String payloadStr = cleanPayload(payload).toJSONString();
        log.info("### payload="+payloadStr);
        Assertions.assertEquals(expectedPayload, payloadStr);
    }
}
