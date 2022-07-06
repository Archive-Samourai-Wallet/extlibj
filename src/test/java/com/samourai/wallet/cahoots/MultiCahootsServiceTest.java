package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.TestUtil;
import com.samourai.wallet.util.TxUtil;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

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
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":6,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };

        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletSender), bipFormatSupplier, params);
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        final HD_Wallet bip84WalletCounterparty = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletCounterparty), bipFormatSupplier, params);
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender
        long spendAmount = 5000;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(spendAmount, "tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3");
        MultiCahoots payload0 = multiCahootsService.startInitiator(cahootsWalletSender, account, contextSender); //grabbed random addr from testnet
        verify(EXPECTED_PAYLOADS[0], payload0);

        // counterparty
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots();
        MultiCahoots payload1 = multiCahootsService.startCollaborator(cahootsWalletCounterparty, account, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1);

        // sender
        MultiCahoots payload2 = multiCahootsService.reply(cahootsWalletSender, contextSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2);

        // counterparty
        MultiCahoots payload3 = multiCahootsService.reply(cahootsWalletCounterparty, contextCp, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3);

        // sender
        MultiCahoots payload4 = multiCahootsService.reply(cahootsWalletSender, contextSender, payload3);
        verify(EXPECTED_PAYLOADS[4], payload4);

        // counterparty
        MultiCahoots payload5 = multiCahootsService.reply(cahootsWalletCounterparty, contextCp, payload4);
        verify(EXPECTED_PAYLOADS[5], payload5);

        // sender
        MultiCahoots payload6 = multiCahootsService.reply(cahootsWalletSender, contextSender, payload5);
        verify(EXPECTED_PAYLOADS[6], payload6);

        Transaction stowawayTx = payload6.getStowaway().getTransaction();
        Assertions.assertEquals("68618508ae3d1166fc40489dd093a8a136141d29633fcb3229861357134358df", stowawayTx.getHashAsString());


        // verify stowaway
        Transaction tx = payload4.getTransaction();
        Assertions.assertEquals("7127186e001f1963137b590e298add049a7d9d89f2ada7280e13b9d76d80b2be", tx.getHashAsString());
        Assertions.assertEquals("02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff04030f00000000000016001440852bf6ea044204b826a182d1b75528364fd0bdeb120000000000001600145fadc28295301797ec5e7c1af71b4cee28dfac328813000000000000160014b57c4444c6a4baa680461186f6558111300a54288813000000000000160014ec1ef246b57d44e3292153339b333685714c0d32024730440220545bb8ae4b02f15b621a22d073255e5279d39f050567c6320ebd7288f9d5462202203ebeb250003c1c57285908d236cc7a102e73a2aec27a884c9757522823a43bbe012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024830450221008d63dd2560ba0a3d2ac8c4fd32df149769cdf9b6b71da7663a23e07c1573e25902203f6547b3248ecb067b055fdd589127d0a4215af433de81472a56e492fe1954df012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000", TxUtil.getInstance().getTxHex(tx));

        // all outputAddresses are bech32
        String[] OUTPUT_ADDRESSES = new String[]{
                "tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4",
                "tb1qt7ku9q54xqte0mz70sd0wx6vac5dltpj3g0m24",
                "tb1qk47yg3xx5ja2dqzxzxr0v4vpzycq54pg8cqqxt",
                "tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3"
        };
        for (TransactionOutput txOutput : tx.getOutputs()) {
            String toAddress = bipFormatSupplier.getToAddress(txOutput);
            Assertions.assertEquals(OUTPUT_ADDRESSES[txOutput.getIndex()], toAddress);
        }

        // verify stonewallx2
        Transaction stonewallx2Tx = payload6.getStonewallx2().getTransaction();
        Assertions.assertEquals("7127186e001f1963137b590e298add049a7d9d89f2ada7280e13b9d76d80b2be", stonewallx2Tx.getHashAsString());

        OUTPUT_ADDRESSES = new String[]{
                "tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4",
                "tb1qt7ku9q54xqte0mz70sd0wx6vac5dltpj3g0m24",
                "tb1qk47yg3xx5ja2dqzxzxr0v4vpzycq54pg8cqqxt",
                "tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3"
        };
        for (TransactionOutput txOutput : tx.getOutputs()) {
            String toAddress = bipFormatSupplier.getToAddress(txOutput);
            Assertions.assertEquals(OUTPUT_ADDRESSES[txOutput.getIndex()], toAddress);
        }
    }
}
