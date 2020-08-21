package com.samourai.wallet.cahoots;

import com.samourai.wallet.segwit.BIP84Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(CahootsServiceTest.class);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {    }

    @Test
    public void Stowaway() throws Exception {
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":1,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":1,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };

        // mock sender wallet
        final BIP84Wallet bip84WalletSender = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(bip84WalletSender, params);
        cahootsWalletSender.mockUTXO("senderTx1", 1, 10000, "senderAddress1");

        // mock receiver wallet
        final BIP84Wallet bip84WalletCounterparty = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(bip84WalletCounterparty, params);
        cahootsWalletCounterparty.mockUTXO("counterpartyTx1", 1, 10000, "counterpartyAddress1");

        // instanciate sender
        long senderFeePerB = 1;
        int senderAccount = 0;
        CahootsService cahootsSender = new CahootsService(params, cahootsWalletSender, senderFeePerB, senderAccount);

        // instanciate receiver
        long receiverFeePerB = 1;
        int receiverAccount = 0; //TODO
        CahootsService cahootsReceiver = new CahootsService(params, cahootsWalletCounterparty, receiverFeePerB, receiverAccount);

        // sender => start Stowaway
        long spendAmount = 5000;
        CahootsMessage payload0 = cahootsSender.newStowaway(spendAmount);
        verify(EXPECTED_PAYLOADS[0], payload0, false, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);

        // receiver => doStowaway1
        CahootsMessage payload1 = cahootsReceiver.reply(payload0);
        verify(EXPECTED_PAYLOADS[1], payload1, false, CahootsType.STOWAWAY, CahootsTypeUser.RECEIVER);

        // sender => doStowaway2
        CahootsMessage payload2 = cahootsSender.reply(payload1);
        verify(EXPECTED_PAYLOADS[2], payload2, false, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);

        // receiver => doStowaway3
        CahootsMessage payload3 = cahootsReceiver.reply(payload2);
        verify(EXPECTED_PAYLOADS[3], payload3, false, CahootsType.STOWAWAY, CahootsTypeUser.RECEIVER);

        // sender => doStowaway4
        CahootsMessage payload4 = cahootsSender.reply(payload3);
        verify(EXPECTED_PAYLOADS[4], payload4, true, CahootsType.STOWAWAY, CahootsTypeUser.SENDER);
    }

    @Test
    public void Stonewallx2() throws Exception {
        final String[] EXPECTED_PAYLOADS = {
                "{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}",
                "{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}"
        };

        // mock sender wallet
        final BIP84Wallet bip84WalletSender = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(bip84WalletSender, params);
        cahootsWalletSender.mockUTXO("senderTx1", 1, 10000, "senderAddress1");

        // mock counterparty wallet
        final BIP84Wallet bip84WalletCounterparty = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(bip84WalletCounterparty, params);
        cahootsWalletCounterparty.mockUTXO("counterpartyTx1", 1, 10000, "counterpartyAddress1");

        // instanciate sender
        long senderFeePerB = 1;
        int senderAccount = 0;
        CahootsService cahootsSender = new CahootsService(params, cahootsWalletSender, senderFeePerB, senderAccount);

        // instanciate counterparty
        long receiverFeePerB = 1;
        int receiverAccount = 0; //TODO
        CahootsService cahootsCounterparty = new CahootsService(params, cahootsWalletCounterparty, receiverFeePerB, receiverAccount);

        // sender => start Stonewallx2
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        CahootsMessage payload0 = cahootsSender.newStonewallx2(spendAmount, address);
        verify(EXPECTED_PAYLOADS[0], payload0, false, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);

        // counterparty => doSTONEWALLx2_1
        CahootsMessage payload1 = cahootsCounterparty.reply(payload0);
        verify(EXPECTED_PAYLOADS[1], payload1, false, CahootsType.STONEWALLX2, CahootsTypeUser.COUNTERPARTY);

        // sender => doSTONEWALLx2_2
        CahootsMessage payload2 = cahootsSender.reply(payload1);
        verify(EXPECTED_PAYLOADS[2], payload2, false, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);

        // counterparty => doSTONEWALLx2_3
        CahootsMessage payload3 = cahootsCounterparty.reply(payload2);
        verify(EXPECTED_PAYLOADS[3], payload3, false, CahootsType.STONEWALLX2, CahootsTypeUser.COUNTERPARTY);

        // sender => doSTONEWALLx2_4
        CahootsMessage payload4 = cahootsSender.reply(payload3);
        verify(EXPECTED_PAYLOADS[4], payload4, true, CahootsType.STONEWALLX2, CahootsTypeUser.SENDER);
    }
}
