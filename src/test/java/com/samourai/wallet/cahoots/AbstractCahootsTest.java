package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.Chain;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.test.AbstractTest;
import com.samourai.wallet.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractCahootsTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsTest.class);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    protected TestCahootsWallet cahootsWalletSender;
    protected TestCahootsWallet cahootsWalletCounterparty;

    protected static String[] SENDER_RECEIVE_84;
    protected static String[] COUNTERPARTY_RECEIVE_84;
    protected static String[] COUNTERPARTY_RECEIVE_44;
    protected static String[] COUNTERPARTY_RECEIVE_49;
    protected static String[] SENDER_CHANGE_84;
    protected static String[] COUNTERPARTY_CHANGE_84;

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

        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        WalletSupplier walletSupplierSender = new WalletSupplierImpl(new MemoryIndexHandlerSupplier(), bip84WalletSender);
        cahootsWalletSender = new TestCahootsWallet(walletSupplierSender, bipFormatSupplier, params);

        final HD_Wallet bip84WalletCounterparty = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        WalletSupplier walletSupplierCounterparty = new WalletSupplierImpl(new MemoryIndexHandlerSupplier(), bip84WalletCounterparty);
        cahootsWalletCounterparty = new TestCahootsWallet(walletSupplierCounterparty, bipFormatSupplier, params);

        SENDER_RECEIVE_84 = new String[4];
        for (int i = 0; i < 4; i++) {
            SENDER_RECEIVE_84[i] = walletSupplierSender.getWallet(BIP_WALLET.DEPOSIT_BIP84).getAddressAt(Chain.RECEIVE.getIndex(), i).getAddressString();
        }

        COUNTERPARTY_RECEIVE_84 = new String[4];
        for (int i = 0; i < 4; i++) {
            COUNTERPARTY_RECEIVE_84[i] = walletSupplierCounterparty.getWallet(BIP_WALLET.DEPOSIT_BIP84).getAddressAt(Chain.RECEIVE.getIndex(), i).getAddressString();
        }

        COUNTERPARTY_RECEIVE_44 = new String[4];
        for (int i = 0; i < 4; i++) {
            COUNTERPARTY_RECEIVE_44[i] = walletSupplierCounterparty.getWallet(BIP_WALLET.DEPOSIT_BIP44).getAddressAt(Chain.RECEIVE.getIndex(), i).getAddressString();
        }

        COUNTERPARTY_RECEIVE_49 = new String[4];
        for (int i = 0; i < 4; i++) {
            COUNTERPARTY_RECEIVE_49[i] = walletSupplierCounterparty.getWallet(BIP_WALLET.DEPOSIT_BIP49).getAddressAt(Chain.RECEIVE.getIndex(), i).getAddressString();
        }

        SENDER_CHANGE_84 = new String[4];
        for (int i = 0; i < 4; i++) {
            SENDER_CHANGE_84[i] = walletSupplierSender.getWallet(BIP_WALLET.DEPOSIT_BIP84).getAddressAt(Chain.CHANGE.getIndex(), i).getAddressString();
        }

        COUNTERPARTY_CHANGE_84 = new String[4];
        for (int i = 0; i < 4; i++) {
            COUNTERPARTY_CHANGE_84[i] = walletSupplierCounterparty.getWallet(BIP_WALLET.DEPOSIT_BIP84).getAddressAt(Chain.CHANGE.getIndex(), i).getAddressString();
        }
    }

    protected Cahoots cleanPayload(String payloadStr) throws Exception {
        Cahoots copy = Cahoots.parse(payloadStr);
        CahootsTestUtil.cleanPayload(copy);
        return copy;
    }

    protected void verify(String expectedPayload, String payloadStr) throws Exception {
        payloadStr = cleanPayload(payloadStr).toJSONString();
        Assertions.assertEquals(expectedPayload, payloadStr);
    }

    protected void verify(String expectedPayload, ManualCahootsMessage cahootsMessage, boolean lastStep, CahootsType type, CahootsTypeUser typeUser) throws Exception {
        verify(expectedPayload, cahootsMessage.getCahoots().toJSONString());
        Assertions.assertEquals(lastStep, cahootsMessage.isDone());
        Assertions.assertEquals(type, cahootsMessage.getType());
        Assertions.assertEquals(typeUser, cahootsMessage.getTypeUser());
    }

    protected Cahoots doCahoots(CahootsWallet cahootsWalletSender, CahootsWallet cahootsWalletCounterparty, AbstractCahootsService cahootsService, CahootsContext cahootsContextSender, CahootsContext cahootsContextCp, String[] EXPECTED_PAYLOADS) throws Exception {
        int nbSteps = EXPECTED_PAYLOADS != null ? EXPECTED_PAYLOADS.length : ManualCahootsMessage.getNbSteps(cahootsContextSender.getCahootsType());

        // sender => _0
        String lastPayload = cahootsService.startInitiator(cahootsWalletSender, cahootsContextSender).toJSONString();
        if (log.isDebugEnabled()) {
            log.debug("#0 SENDER => "+lastPayload);
        }
        if (EXPECTED_PAYLOADS != null) {
            verify(EXPECTED_PAYLOADS[0], lastPayload);
        }

        // counterparty => _1
        lastPayload = cahootsService.startCollaborator(cahootsWalletCounterparty, cahootsContextCp, Cahoots.parse(lastPayload)).toJSONString();
        if (log.isDebugEnabled()) {
            log.debug("#1 COUNTERPARTY => "+lastPayload);
        }
        if (EXPECTED_PAYLOADS != null) {
            verify(EXPECTED_PAYLOADS[1], lastPayload);
        }

        for (int i=2; i<nbSteps; i++) {
            if (i%2 == 0) {
                // sender
                lastPayload = cahootsService.reply(cahootsWalletSender, cahootsContextSender, Cahoots.parse(lastPayload)).toJSONString();
                if (log.isDebugEnabled()) {
                    log.debug("#"+i+" SENDER => "+lastPayload);
                }
            } else {
                // counterparty
                lastPayload = cahootsService.reply(cahootsWalletCounterparty, cahootsContextCp, Cahoots.parse(lastPayload)).toJSONString();
                if (log.isDebugEnabled()) {
                    log.debug("#"+i+" COUNTERPARTY => "+lastPayload);
                }
            }
            if (EXPECTED_PAYLOADS != null) {
                verify(EXPECTED_PAYLOADS[i], lastPayload);
            }
        }
        Cahoots cahoots = Cahoots.parse(lastPayload);
        cahoots.pushTx(pushTx);
        return cahoots;
    }
}
