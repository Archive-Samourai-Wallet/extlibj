package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractCahootsTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsTest.class);

    protected IndexHandlerSupplier indexHandlerSupplier = new MemoryIndexHandlerSupplier();
    protected Stonewallx2Service stonewallx2Service = new Stonewallx2Service(bipFormatSupplier, params) {
        @Override
        protected void shuffleUtxos(List<CahootsUtxo> utxos) {
            // no shuffle
        }
    };
    protected StowawayService stowawayService = new StowawayService(bipFormatSupplier, params) {
        @Override
        protected int getRandNextInt(int bound) {
            return 0; // make test reproductible
        }

        @Override
        protected void shuffleUtxos(List<CahootsUtxo> utxos) {
            // no shuffle
        }
    };
    protected MultiCahootsService multiCahootsService = new MultiCahootsService(bipFormatSupplier, params, stonewallx2Service, stowawayService, xManagerClient);

    public void setUp() throws Exception {
        super.setUp();
    }

    protected Cahoots cleanPayload(Cahoots payload) throws Exception {
        Cahoots copy = Cahoots.parse(payload.toJSONString());
        CahootsTestUtil.cleanPayload(copy);
        return copy;
    }

    protected void verify(String expectedPayload, Cahoots cahoots) throws Exception {
        String payloadStr = cleanPayload(cahoots).toJSONString();
        Assertions.assertEquals(expectedPayload, payloadStr);
    }

    protected void verify(String expectedPayload, ManualCahootsMessage cahootsMessage, boolean lastStep, CahootsType type, CahootsTypeUser typeUser) throws Exception {
        verify(expectedPayload, cahootsMessage.getCahoots());
        Assertions.assertEquals(lastStep, cahootsMessage.isDone());
        Assertions.assertEquals(type, cahootsMessage.getType());
        Assertions.assertEquals(typeUser, cahootsMessage.getTypeUser());
    }
}
