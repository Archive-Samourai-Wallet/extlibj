package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsTest.class);

    protected NetworkParameters params = TestNet3Params.get();
    protected BipFormatSupplier bipFormatSupplier = BIP_FORMAT.PROVIDER;
    protected IndexHandlerSupplier indexHandlerSupplier = new MemoryIndexHandlerSupplier();

    protected Cahoots cleanPayload(Cahoots payload) throws Exception {
        Cahoots copy = Cahoots.parse(payload.toJSONString());
        CahootsTestUtil.cleanPayload(copy);
        return copy;
    }

    protected void verify(String expectedPayload, Cahoots cahoots) throws Exception {
        String payloadStr = cleanPayload(cahoots).toJSONString();
        System.out.println(cahoots.getStep() + ": " + payloadStr);
        Assertions.assertEquals(expectedPayload, payloadStr);
    }

    protected void verify(String expectedPayload, ManualCahootsMessage cahootsMessage, boolean lastStep, CahootsType type, CahootsTypeUser typeUser) throws Exception {
        verify(expectedPayload, cahootsMessage.getCahoots());
        Assertions.assertEquals(lastStep, cahootsMessage.isDone());
        Assertions.assertEquals(type, cahootsMessage.getType());
        Assertions.assertEquals(typeUser, cahootsMessage.getTypeUser());
    }
}
