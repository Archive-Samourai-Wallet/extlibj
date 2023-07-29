package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.*;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ManualCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(ManualCahootsServiceTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void Stowaway() throws Exception {
        int account = 0;
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":210,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":210,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":210,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };

        // mock sender wallet
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        // mock receiver wallet
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => start Stowaway
        long spendAmount = 5000;
        StowawayContext contextSender = StowawayContext.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount);
        ManualCahootsMessage payload0 = manualCahootsService.initiate(contextSender);
        verify(EXPECTED_PAYLOADS[0], payload0, false, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);

        // receiver => doStowaway1
        StowawayContext contextReceiver = StowawayContext.newCounterparty(cahootsWalletCounterparty, account);
        ManualCahootsMessage payload1 = (ManualCahootsMessage)manualCahootsService.reply(contextReceiver, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1, false, CahootsType.STOWAWAY, CahootsTypeUser.COUNTERPARTY);

        // sender => doStowaway2
        ManualCahootsMessage payload2 = (ManualCahootsMessage)manualCahootsService.reply(contextSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2, false, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);

        // receiver => doStowaway3
        ManualCahootsMessage payload3 = (ManualCahootsMessage)manualCahootsService.reply(contextReceiver, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3, false, CahootsType.STOWAWAY, CahootsTypeUser.COUNTERPARTY);

        // sender => interaction TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)manualCahootsService.reply(contextSender, payload3);
        Assertions.assertEquals(TypeInteraction.TX_BROADCAST, txBroadcastInteraction.getTypeInteraction());

        // sender => doStowaway4
        ManualCahootsMessage payload4 = (ManualCahootsMessage)txBroadcastInteraction.getReplyAccept();
        verify(EXPECTED_PAYLOADS[4], payload4, true, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);
        Cahoots cahoots = payload4.getCahoots();

        // verify TX
        String txid = "e5c6df03befcf1e70382066d2011186383d53c7d9a48f13f5f16f2557d858252";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b6120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100d3e7e7ed9c42631ea56730ddf04e7f4d08d3e79c54c6b4a09482c801ccf5717002200cce706f9a5bfec189d75c8cd3e7d87ba6bdc8841e34c7ebd68029981730a91f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e95efdf8247859d5f59299f70631e8c9154dcec58fac670064b9f7252acfe1c002204ead98bf5a21d16e06a5dc97f7573d025c7b314506079d63fb5f4296daab32af012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4790L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
    }

    @Test
    public void Stonewallx2() throws Exception {
        int account = 0;
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };

        // mock sender wallet
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        // mock counterparty wallet
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => start Stonewallx2
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
            CahootsContext contextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        ManualCahootsMessage payload0 = manualCahootsService.initiate(contextSender);
        verify(EXPECTED_PAYLOADS[0], payload0, false, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);

        // counterparty => doSTONEWALLx2_1
        CahootsContext contextCounterparty = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);
        ManualCahootsMessage payload1 = (ManualCahootsMessage)manualCahootsService.reply(contextCounterparty, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1, false, CahootsType.STONEWALLX2, CahootsTypeUser.COUNTERPARTY);

        // sender => doSTONEWALLx2_2
        ManualCahootsMessage payload2 = (ManualCahootsMessage)manualCahootsService.reply(contextSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2, false, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);

        // counterparty => doSTONEWALLx2_3
        ManualCahootsMessage payload3 = (ManualCahootsMessage)manualCahootsService.reply(contextCounterparty, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3, false, CahootsType.STONEWALLX2, CahootsTypeUser.COUNTERPARTY);

        // sender => interaction TX_BROADCAST
        SorobanInteraction payload4Interaction = (SorobanInteraction)manualCahootsService.reply(contextSender, payload3);

        // sender => doSTONEWALLx2_4
        ManualCahootsMessage payload4 = (ManualCahootsMessage)payload4Interaction.getReplyAccept();
        verify(EXPECTED_PAYLOADS[4], payload4, true, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);
        Cahoots cahoots = payload4.getCahoots();

        // verify TX
        String txid = "7455f15769b0f876bf3853cf20ef60090255bffea5dd26e5a521eb2dd0b88faf";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04001300000000000016001440852bf6ea044204b826a182d1b75528364fd0bd00130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02473044022079a012b8bc5197e8c0ff1750f91432d78cbb6e742f76e512be05739ec648525402206316231492a7b4789c93487d08d04d1417688dec863315e3a59cf97533e552b9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df600bb360487f1f2b48438c77f85978c96353794357fca8de8d3c59898dcf7a022076f284c4bc7142ee94d536e36ffea126481c3aa48899ff7853d7dd69e05b3880012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4864L);
        outputs.put(SENDER_CHANGE_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
    }

    @Test
    public void multiCahoots() throws Exception {
        int account = 0;
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"step\":0,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":-1,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":1,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":-1,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":2,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":-1,\"outpoints\":[],\"type\":1,\"params\":\"testnet\",\"dest\":\"\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":3,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":311,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":4,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":311,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":210,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
                "{\"cahoots\":{\"step\":5,\"type\":2,\"params\":\"testnet\",\"version\":2},\"stonewallx2\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"destPaynym\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":272,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"tb1qgzzjhah2q3pqfwpx5xpdrd649qmyl59a6m6cp4\",\"id\":\"testID\",\"account\":0,\"ts\":123456}},\"stowaway\":{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":311,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"}],\"type\":1,\"params\":\"testnet\",\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"version\":2,\"fee_amount\":210,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"id\":\"testID\",\"account\":0,\"ts\":123456}}}",
        };

        // mock sender wallet
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        // mock receiver wallet
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => start multiCahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = MultiCahootsContext.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        ManualCahootsMessage payload0 = manualCahootsService.initiate(contextSender);
        verify(EXPECTED_PAYLOADS[0], payload0, false, CahootsType.MULTI, CahootsTypeUser.SENDER);

        // counterparty
        CahootsContext contextReceiver = MultiCahootsContext.newCounterparty(cahootsWalletCounterparty, account, xManagerClient);
        ManualCahootsMessage payload1 = (ManualCahootsMessage)manualCahootsService.reply(contextReceiver, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1, false, CahootsType.MULTI, CahootsTypeUser.COUNTERPARTY);

        // sender
        ManualCahootsMessage payload2 = (ManualCahootsMessage)manualCahootsService.reply(contextSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2, false, CahootsType.MULTI, CahootsTypeUser.SENDER);

        // counterparty
        ManualCahootsMessage payload3 = (ManualCahootsMessage)manualCahootsService.reply(contextReceiver, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3, false, CahootsType.MULTI, CahootsTypeUser.COUNTERPARTY);

        // sender
        ManualCahootsMessage payload4 = (ManualCahootsMessage)manualCahootsService.reply(contextSender, payload3);
        verify(EXPECTED_PAYLOADS[4], payload4, false, CahootsType.MULTI, CahootsTypeUser.SENDER);

        // counterparty
        ManualCahootsMessage payload5 = (ManualCahootsMessage)manualCahootsService.reply(contextReceiver, payload4);
        verify(EXPECTED_PAYLOADS[5], payload5, false, CahootsType.MULTI, CahootsTypeUser.COUNTERPARTY);

        // sender => interaction TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)manualCahootsService.reply(contextSender, payload5);
        Assertions.assertEquals(TypeInteraction.TX_BROADCAST_MULTI, txBroadcastInteraction.getTypeInteraction());

        Cahoots cahoots = payload5.getCahoots();

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "7455f15769b0f876bf3853cf20ef60090255bffea5dd26e5a521eb2dd0b88faf";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04001300000000000016001440852bf6ea044204b826a182d1b75528364fd0bd00130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02473044022079a012b8bc5197e8c0ff1750f91432d78cbb6e742f76e512be05739ec648525402206316231492a7b4789c93487d08d04d1417688dec863315e3a59cf97533e552b9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df600bb360487f1f2b48438c77f85978c96353794357fca8de8d3c59898dcf7a022076f284c4bc7142ee94d536e36ffea126481c3aa48899ff7853d7dd69e05b3880012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4864L);
            outputs.put(SENDER_CHANGE_84[0], 4864L);
            verifyTx(tx, txid, raw, outputs);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "2e782010171546ebafeb6d412b224fdf410d257bcc2227bdcd75e2784533543d";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02371d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c82295f2400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100ea9f18b8d262e90b525909c214a24c20783874799a749d92262436f1bc889df702205d16e831df6f6b0da7d000ab93d0fc8d4759d63dbec9f0afc2a98ff362855d21012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024730440220364714c1fd2390638c1f73e8815f582ad4c7529658ccffd3c6292af9cc8dbd2002205ae693c62616db54f505e5ec1d590b4956da2c3af44b7213a00c90d958d5d420012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9311L);
            outputs.put(SENDER_CHANGE_84[1], 7479L);
            verifyTx(tx, txid, raw, outputs);
        }
    }
}
