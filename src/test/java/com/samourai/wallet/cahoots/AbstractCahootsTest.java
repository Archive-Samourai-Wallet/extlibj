package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.client.indexHandler.IndexHandlerSupplier;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.test.AbstractTest;
import com.samourai.wallet.util.TxUtil;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
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

    protected Cahoots doCahoots(CahootsWallet cahootsWalletSender, CahootsWallet cahootsWalletCounterparty, AbstractCahootsService cahootsService, CahootsContext cahootsContextSender, CahootsContext cahootsContextCp, String[] EXPECTED_PAYLOADS) throws Exception {
        int nbSteps = EXPECTED_PAYLOADS != null ? EXPECTED_PAYLOADS.length : ManualCahootsMessage.getNbSteps(cahootsContextSender.getCahootsType());

        // sender => _0
        Cahoots lastPayload = cahootsService.startInitiator(cahootsWalletSender, cahootsContextSender);
        if (log.isDebugEnabled()) {
            log.debug("#0 SENDER => "+lastPayload.toJSONString());
        }
        if (EXPECTED_PAYLOADS != null) {
            verify(EXPECTED_PAYLOADS[0], lastPayload);
        }

        // counterparty => _1
        lastPayload = cahootsService.startCollaborator(cahootsWalletCounterparty, cahootsContextCp, lastPayload);
        if (log.isDebugEnabled()) {
            log.debug("#1 COUNTERPARTY => "+lastPayload.toJSONString());
        }
        if (EXPECTED_PAYLOADS != null) {
            verify(EXPECTED_PAYLOADS[1], lastPayload);
        }

        for (int i=2; i<nbSteps; i++) {
            if (i%2 == 0) {
                // sender
                lastPayload = cahootsService.reply(cahootsWalletSender, cahootsContextSender, lastPayload);
                if (log.isDebugEnabled()) {
                    log.debug("#"+i+" SENDER => "+lastPayload.toJSONString());
                }
            } else {
                // counterparty
                lastPayload = cahootsService.reply(cahootsWalletCounterparty, cahootsContextCp, lastPayload);
                if (log.isDebugEnabled()) {
                    log.debug("#"+i+" COUNTERPARTY => "+lastPayload.toJSONString());
                }
            }
            if (EXPECTED_PAYLOADS != null) {
                verify(EXPECTED_PAYLOADS[i], lastPayload);
            }
        }
        return lastPayload;
    }

    protected void verifyTx(Transaction tx, String txid, String raw, String[] OUTPUT_ADDRESSES) throws Exception {
        Assertions.assertEquals(txid, tx.getHashAsString());
        Assertions.assertEquals(raw, TxUtil.getInstance().getTxHex(tx));

        for (TransactionOutput txOutput : tx.getOutputs()) {
            String toAddress = bipFormatSupplier.getToAddress(txOutput);
            Assertions.assertEquals(OUTPUT_ADDRESSES[txOutput.getIndex()], toAddress);
        }
    }
}
