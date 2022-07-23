package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class Stonewallx2ServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2ServiceTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void STONEWALLx2_BIP84() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(account, spendAmount, address);
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2(account);

        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };
        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, EXPECTED_PAYLOADS);

        // verify TX
        String txid = "e896b8c0194070ea2692af11e808f6f128bb4b533917011ac913b4f6fc48fc95";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000016001428a90fa3f4f285fc689f389115326dbf96917d6288130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550247304402204baaacb3787465c84f29717af596ec6c946008934ba06df317515aeaa62c78fd022012858f397fa9708e9b08382fc231fb9ab59a1abe3ee830495e13eddebd050e5a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f530b6c177a1a0145b359261ee5a90adc825ad3f5c2e6777cce035d1172ba7ca022049326f6a99b91129f83f1db77eca60617e6d7b833406bfc5a7a9bfdca26c5a04012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_RECEIVE_84[0], spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP44() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(account, spendAmount, address);
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "0b7542c1293540d6a6a29aa0f47b346e899d092a5fdd485727487add68badf41";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a914000e21083c170b758f2c3e5ae2beabac44df1e6988ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100879699c3b16a76960626a46c39be48853b37d631bb9917e5d3bc9a0d6f43c46802207709618870a5f198679845d4d9388c2aa51d80e443ebe00ce11e58749cca742e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022078126a426e9160fc0fbd7592dd03382cd60e9ae12d1aa157b8bcc98d787fbd7f02201e48f36ab78365da71673b383f024cd2430b4642fad570836f1316dcb6628dfb012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_RECEIVE_44[0], spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP49() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP49;
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(account, spendAmount, address);
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "9c9aa7190c796de043314b70e071ab20d62f02bb32496e33126a32bd21b0da32";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287881300000000000017a9145a32957bb7ed3b76d2d807b823316414f393da18870247304402202e63086b8de1623797bbaea2fd654460f54eb2de38cbf34df4bb6b293d89306102203bc9696d665ea5691f723e9a6248c5c7e476f85c41ec8244ae5f949e60d44ea1012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20248304502210088378fb4deeb1d6a456f95c1ec354615c570381661e6ab7dc07946df939496f402204a316e756081f3ae9d7eed00eaa7d63662a8c2da1fc3997c4e0e8dbf25fd38d7012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_RECEIVE_49[0], spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void invalidStonewallExcetion() throws Exception {
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";

        // throw Exception for 0 spend amount
        Assertions.assertThrows(Exception.class,
                () -> {
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, 0, address);
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });

        // throw Exception for blank address
        Assertions.assertThrows(Exception.class,
                () -> {
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, 0, "");
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });
    }
}
