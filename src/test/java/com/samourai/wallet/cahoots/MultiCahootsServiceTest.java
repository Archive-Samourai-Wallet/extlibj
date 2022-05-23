package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.TestUtil;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

    private Stonewallx2Service stonewallx2Service = new Stonewallx2Service(bipFormatSupplier, params) {
        @Override
        protected void shuffleUtxos(List<CahootsUtxo> utxos) {
            // no shuffle
        }
    };
    private StowawayService stowawayService = new StowawayService(bipFormatSupplier, params) {
        @Override
        protected int getRandNextInt(int bound) {
            return 0; // make test reproductible
        }

        @Override
        protected void shuffleUtxos(List<CahootsUtxo> utxos) {
            // no shuffle
        }
    };
    private MultiCahootsService multiCahootsService = new MultiCahootsService(bipFormatSupplier, params, stonewallx2Service, stowawayService);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void multiCahoots() throws Exception {
        int account = 0;

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[],\"type\":1,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"step\":5,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"}],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":562,\"step\":6,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":562,\"step\":7,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":562,\"step\":8,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };

        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletSender), bipFormatSupplier, params);
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        final HD_Wallet bip84WalletCounterparty = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletCounterparty), bipFormatSupplier, params);
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => doStowaway0
        long spendAmount = 5000;
        MultiCahoots payload0 = multiCahootsService.startInitiator(cahootsWalletSender, "tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3", spendAmount, account); //grabbed random addr from testnet
        verify(EXPECTED_PAYLOADS[0], payload0);

        // counterparty => doStowaway1
        MultiCahoots payload1 = multiCahootsService.startCollaborator(cahootsWalletCounterparty, account, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1);

        // sender => doStowaway2
        MultiCahoots payload2 = multiCahootsService.reply(cahootsWalletSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2);

        // counterparty => doStowaway3
        MultiCahoots payload3 = multiCahootsService.reply(cahootsWalletCounterparty, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3);

        // sender => doStowaway4
        MultiCahoots payload4 = multiCahootsService.reply(cahootsWalletSender, payload3);
        verify(EXPECTED_PAYLOADS[4], payload4);

        // counterparty => doStonewall1
        MultiCahoots payload5 = multiCahootsService.reply(cahootsWalletCounterparty, payload4);
        verify(EXPECTED_PAYLOADS[5], payload5);

        // sender => doStonewall2
        MultiCahoots payload6 = multiCahootsService.reply(cahootsWalletSender, payload5);
        verify(EXPECTED_PAYLOADS[6], payload6);

        // counterparty => doStonewall3
        MultiCahoots payload7 = multiCahootsService.reply(cahootsWalletCounterparty, payload6);
        verify(EXPECTED_PAYLOADS[7], payload7);

        // sender => doStonewall4
        MultiCahoots payload8 = multiCahootsService.reply(cahootsWalletCounterparty, payload7);
        verify(EXPECTED_PAYLOADS[8], payload8);

        Transaction stowawayTx = payload8.getStowaway().getTransaction();
        System.out.println(stowawayTx.toString());
        Assertions.assertEquals("ea9006dda17a9ccd3c92ed7148d8f78616fb2164301eb3b4467234b2cf5eefb5", stowawayTx.getHashAsString());

        Transaction stonewallx2Tx = payload8.getStonewallx2().getTransaction();
        System.out.println(stonewallx2Tx.toString());
        Assertions.assertEquals("7127186e001f1963137b590e298add049a7d9d89f2ada7280e13b9d76d80b2be", stonewallx2Tx.getHashAsString());
    }
}
