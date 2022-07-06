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

import java.util.LinkedHashMap;
import java.util.Map;

public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void multiCahoots() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(account, spendAmount, address);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(account);

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":6,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":575,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };
        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, EXPECTED_PAYLOADS);

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "68618508ae3d1166fc40489dd093a8a136141d29633fcb3229861357134358df";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02091c0000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02044f2900000000000016001428a90fa3f4f285fc689f389115326dbf96917d62024730440220611270bf7377339f836e8f29d54683b607d69693625cf9e0e2ee961167ee6ca1022076887cb6a6608676f0b72336bc1ee0d956d2565e60f650f438812e53d776a986012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024830450221009200017802aaabe03c41ab2fd5824730ee1e6815c8d4a622f594fd919f8850520220108dab0d139d7fe770fe4678fc765881ef35bedb8d33aa9231c4af74c7074a61012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 10575L);
            outputs.put(SENDER_CHANGE_84[0], 7177L);
            verifyTx(tx, txid, raw, outputs);
        }

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "3a5b354c008e2fd65fc34bcf661ab2cc82ae3bdce70641c3a7847a0c7a6ef1b5";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff04030f000000000000160014657b6afdeef6809fdabce7face295632fbd94febeb1200000000000016001485963b79fea38b84ce818e5f29a5a115bd4c822988130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014b57c4444c6a4baa680461186f6558111300a5428024830450221008a62c0ec07a2ed9b5c573af3cf8cb336404a86af6cca25262d2c8566bb938f470220434121f81356275445d360f075e665a0b41be400b77cc55c22f95eef83222301012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022014c58beda1c36ca6b5a21eda88f958b9db569e895f2efc788b0c68ec8e8fa199022015a058363f41d09846d8b5946e77538d317d3dc2fa7cbefcf3e8f36dd5b22501012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_RECEIVE_84[1], spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], 3843L);
            outputs.put(SENDER_CHANGE_84[1], 4843L);
            verifyTx(tx, txid, raw, outputs);
        }
    }
}
