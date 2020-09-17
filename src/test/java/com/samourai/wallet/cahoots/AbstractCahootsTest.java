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

    protected Cahoots cleanPayload(Cahoots payload) throws Exception {
        Cahoots copy = Cahoots.parse(payload.toJSONString());
        CahootsTestUtil.cleanPayload(copy);
        return copy;
    }

    protected void verify(String expectedPayload, Cahoots cahoots) throws Exception {
        String payloadStr = cleanPayload(cahoots).toJSONString();
        log.info("### payload="+payloadStr);
        Assertions.assertEquals(expectedPayload, payloadStr);
    }

    protected void verify(String expectedPayload, ManualCahootsMessage cahootsMessage, boolean lastStep, CahootsType type, CahootsTypeUser typeUser) throws Exception {
        verify(expectedPayload, cahootsMessage.getCahoots());
        Assertions.assertEquals(lastStep, cahootsMessage.isDone());
        Assertions.assertEquals(type, cahootsMessage.getType());
        Assertions.assertEquals(typeUser, cahootsMessage.getTypeUser());
    }
}
