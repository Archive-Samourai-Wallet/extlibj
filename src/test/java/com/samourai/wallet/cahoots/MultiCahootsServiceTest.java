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
    public void multiCahoots_bip84() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(account, feePerB, address);
        contextSender.setAmount(spendAmount);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(account);

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":6,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };
        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, EXPECTED_PAYLOADS);

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "215908d4b1ee882d665c74883b619fb446879b7d3575f6799efd73144d70bc19";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02991d0000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204bf2700000000000016001428a90fa3f4f285fc689f389115326dbf96917d620247304402201cef4fd20ff91b183b29b9daedcfa50a8c696c34ef1064f5e8cdb4b7a8c53e770220780d4d7cd5d427145fdd3496d9422add443eb8a669bf4ca8fc838fc521d3a0ee012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100ab36d41acee5c4063d5afdcd4d6bb2a12b76620834ff54b4dd63e2e79357855902206f33e633892edcfb96aef8539daa6d98c086cea9e9b8c2eae3f711100d82843a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 10175L);
            outputs.put(SENDER_CHANGE_84[0], 7577L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "197af328c403d7575e0a06f2c2f3fd34d0375caebad5de9ba528d8fd53603358";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94febeb1200000000000016001485963b79fea38b84ce818e5f29a5a115bd4c822988130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014b57c4444c6a4baa680461186f6558111300a54280247304402207817d6840fc664f719a8f08937d6ca6e2c0cf00760fd6510db5e491f914cd5ab0220690a291bb9768e4465b88a92e7eeccc077ad14fc0af18f33f4af0af84bc433ab012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20248304502210085fdbd913287f3fef2d22003bd99142e0bf458df658f73401bdaf7b02130fee702201a0d12d7780246f8e28a3fc28c41f59810e1d1d77021bcd4cf7992d7a18ba777012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_RECEIVE_84[1], spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
            outputs.put(SENDER_CHANGE_84[1], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_bip84_extract() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        // counterparty > THRESHOLD
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx3", 1, 550000000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(account, feePerB, address);
        contextSender.setAmount(spendAmount);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(account);

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":6,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":550000000,\"outpoint\":\"bc64c010e0cc534f4b3e80a65918f53d2d4acf9a049a613b417b9fc4d2c760f9-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };
        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, EXPECTED_PAYLOADS);

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "183ed22f980ce34e6c52556e702d6aedcefdb38f49f6f9e2cf28c174562ffff6";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdfffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000fdffffff02991d0000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02042f56c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100cc0cfca627625d454680937f61e1674ad27d9ccd4baac1e7c86449e6ae8fcd2f02202ca70f9528af2afb81aac9d2fe3e63042f094125f314689d879c874ab7064544012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100ee72f4c6937166a59d603b695409d2c7c2af258cfce686706439429e747d03a90220375eace1e4f1a636a9bd25c23ed9fcb0f3fffb339b82c4df43700f66826e2926012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 550000175L);
            outputs.put(SENDER_CHANGE_84[0], 7577L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "649eab2dbf8cde79164600d759568a29fdf14949243c6e51041b5024f571bad5";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94febeb1200000000000016001485963b79fea38b84ce818e5f29a5a115bd4c822988130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f52730247304402201c14497cb7a836208eb0075c7668eb910756f1ac809c3bd30814a717655db6fd022017bdd08cf166eb476a51030124d47aa90a709745bfa7333dc6b5100cabdf9711012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022057fcef06254fbfcf97fe98c5c3afac8903eab209a0f0292d9fb491c51d64d7ee0220204952990d1f70df8140df0f886af6088a07673e67c1b6a6894b25d5176ecc6a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(ADDRESS_XMANAGER, spendAmount); // extracting to external
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
            outputs.put(SENDER_CHANGE_84[1], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_bip44() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(account, feePerB, address);
        contextSender.setAmount(spendAmount);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(account);

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":6,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"muimRQFJKMJM1pTminJxiD5HrPgSu257tX\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":175,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}}"
        };
        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, EXPECTED_PAYLOADS);

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "215908d4b1ee882d665c74883b619fb446879b7d3575f6799efd73144d70bc19";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02991d0000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204bf2700000000000016001428a90fa3f4f285fc689f389115326dbf96917d620247304402201cef4fd20ff91b183b29b9daedcfa50a8c696c34ef1064f5e8cdb4b7a8c53e770220780d4d7cd5d427145fdd3496d9422add443eb8a669bf4ca8fc838fc521d3a0ee012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100ab36d41acee5c4063d5afdcd4d6bb2a12b76620834ff54b4dd63e2e79357855902206f33e633892edcfb96aef8539daa6d98c086cea9e9b8c2eae3f711100d82843a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 10175L);
            outputs.put(SENDER_CHANGE_84[0], 7577L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "e4416a7ac9073b049e56c122cd5c1405d1332f15c5828643beb7884705bb08d7";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94febeb1200000000000016001485963b79fea38b84ce818e5f29a5a115bd4c822988130000000000001976a914000e21083c170b758f2c3e5ae2beabac44df1e6988ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac0247304402203229485c6ec93d566dd717897b149cad6df17b2a43b41ba27728ddc5efe2df800220484b99b4878a63fc3c2df93d6327bbb04c7a0420da66c9bad7ec0f9756050890012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100b200e2ae303b7fea981f89e2dd2a492916f8397d12061a80303aa7713cb32bb0022070052235d863499a443e30c78a91ec23f7e8ce017a42b37ff2c993f49faf1cc5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_RECEIVE_44[0], spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
            outputs.put(SENDER_CHANGE_84[1], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
