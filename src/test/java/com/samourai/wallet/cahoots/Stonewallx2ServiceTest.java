package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
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

public class Stonewallx2ServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2ServiceTest.class);

    private Stonewallx2Service stonewallx2Service = new Stonewallx2Service(bipFormatSupplier, params);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {    }

    @Test
    public void STONEWALLx2() throws Exception {
        int account = 0;
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"params\":\"testnet\",\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };

        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletSender), bipFormatSupplier, params);
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        final HD_Wallet bip84WalletCounterparty = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletCounterparty), bipFormatSupplier, params);
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => doSTONEWALLx2_0
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(spendAmount, address);
        STONEWALLx2 payload0 = stonewallx2Service.startInitiator(cahootsWalletSender, account, cahootsContextSender);
        verify(EXPECTED_PAYLOADS[0], payload0);

        // counterparty => doSTONEWALLx2_1
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2();
        STONEWALLx2 payload1 = stonewallx2Service.startCollaborator(cahootsWalletCounterparty, account, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1);

        // sender => doSTONEWALLx2_2
        STONEWALLx2 payload2 = stonewallx2Service.reply(cahootsWalletSender, cahootsContextSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2);

        // counterparty => doSTONEWALLx2_3
        STONEWALLx2 payload3 = stonewallx2Service.reply(cahootsWalletCounterparty, cahootsContextCp, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3);

        // sender => doSTONEWALLx2_4
        STONEWALLx2 payload4 = stonewallx2Service.reply(cahootsWalletSender, cahootsContextSender, payload3);
        verify(EXPECTED_PAYLOADS[4], payload4);

        // verify
        Transaction tx = payload4.getTransaction();
        Assertions.assertEquals("c9a3da30bcec70069030a9f25a91e9a9aaee71b498383229f76eda84b398e88c", tx.getHashAsString());
        Assertions.assertEquals("02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94febeb1200000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229881300000000000016001428a90fa3f4f285fc689f389115326dbf96917d6288130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d127355024830450221009dfb70f0c4b54bbe60dff0ad8a49a6fc8ee8136bd04d553a62d41cc39a8b2e8602206b3961a25d1d32d3cc07e6d91b731764b858c6e8f07f135643e0d21631f8c3b5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f3999d41aa1f1d2d75a5049dd27df303e477fb6af9357b2126645f56af56dc22022003fc958f62ebb43addfcd988e544606c690d54da477371ee57aa80a0838c468c012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000", TxUtil.getInstance().getTxHex(tx));

        // all outputAddresses are bech32
        String[] OUTPUT_ADDRESSES = new String[]{
                "tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5",
                "tb1qsktrk7075w9cfn5p3e0jnfdpzk75eq3f5qced4",
                "tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w",
                "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4"
        };
        for (TransactionOutput txOutput : tx.getOutputs()) {
            String toAddress = bipFormatSupplier.getToAddress(txOutput);
            Assertions.assertEquals(OUTPUT_ADDRESSES[txOutput.getIndex()], toAddress);
        }
    }

    @Test
    public void invalidStonewallExcetion() throws Exception {
        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletSender), bipFormatSupplier, params);
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";

        // throw Exception for 0 spend amount
        Assertions.assertThrows(Exception.class,
                () -> {
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, address);
                    stonewallx2Service.startInitiator(cahootsWalletSender, 0, cahootsContextSender);
                });

        // throw Exception for blank address
        Assertions.assertThrows(Exception.class,
                () -> {
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, "");
                    stonewallx2Service.startInitiator(cahootsWalletSender, 0, cahootsContextSender);
                });
    }
}
