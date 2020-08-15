package com.samourai.wallet.cahoots;

import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.java.HD_WalletFactoryJava;
import com.samourai.wallet.segwit.BIP84Wallet;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STONEWALLx2ServiceTest {
    private static final Logger log = LoggerFactory.getLogger(STONEWALLx2ServiceTest.class);

    private HD_WalletFactoryJava hdWalletFactory = HD_WalletFactoryJava.getInstance();
    private NetworkParameters params = TestNet3Params.get();

    private STONEWALLx2Service stonewallx2Service = new STONEWALLx2Service(params);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {    }

    @Test
    public void STONEWALLx2() throws Exception {
        final BIP84Wallet bip84WalletSender = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(bip84WalletSender, params);
        cahootsWalletSender.mockUTXO("senderTx1", 1, 10000, "senderAddress1");
        //cahootsWalletSender.mockUTXO("senderTx2", 2, 20000, "senderAddress2");

        final BIP84Wallet bip84WalletCounterparty = computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(bip84WalletCounterparty, params);
        cahootsWalletCounterparty.mockUTXO("counterpartyTx1", 1, 10000, "counterpartyAddress1");
        //cahootsWalletCounterparty.mockUTXO("counterpartyTx2", 2, 20000, "counterpartyAddress2");

        // sender
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        int account = 0;

        // sender => doSTONEWALLx2_0
        STONEWALLx2 payload_0 = stonewallx2Service.startInitiator(cahootsWalletSender, spendAmount, address, account);

        // use static values for test
        payload_0.strID = "testID";
        payload_0.ts = 123456;

        log.info("payload_0="+payload_0.toJSON());
        Assertions.assertEquals("{\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}", payload_0.toJSON().toString());

        // counterparty => doSTONEWALLx2_1
        long feePerB = 1;
        STONEWALLx2 payload_1 = stonewallx2Service.startCollaborator(payload_0, cahootsWalletCounterparty, account);
        log.info("payload_1="+payload_1.toJSON());
        STONEWALLx2 payload_1_ = new STONEWALLx2(payload_1); // TODO use static values for test
        payload_1_.psbt = null;
        Assertions.assertEquals("{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}", payload_1_.toJSON().toString());

        // sender => doSTONEWALLx2_2
        STONEWALLx2 payload_2 = stonewallx2Service.resume(payload_1, cahootsWalletSender, feePerB);
        log.info("payload_2="+payload_2.toJSON());
        STONEWALLx2 payload_2_ = new STONEWALLx2(payload_2); // TODO use static values for test
        payload_2_.psbt = null;
        Assertions.assertEquals("{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}", payload_2_.toJSON().toString());

        // counterparty => doSTONEWALLx2_3
        STONEWALLx2 payload_3 = stonewallx2Service.resume(payload_2, cahootsWalletCounterparty, feePerB);
        log.info("payload_3="+payload_3.toJSON());
        STONEWALLx2 payload_3_ = new STONEWALLx2(payload_3); // TODO use static values for test
        payload_3_.psbt = null;
        Assertions.assertEquals("{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}", payload_3_.toJSON().toString());

        // sender => doSTONEWALLx2_4
        STONEWALLx2 payload_4 = stonewallx2Service.resume(payload_3, cahootsWalletSender, feePerB);
        log.info("payload_4="+payload_4.toJSON());
        STONEWALLx2 payload_4_ = new STONEWALLx2(payload_4); // TODO use static values for test
        payload_4_.psbt = null;
        Assertions.assertEquals("{\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":0,\"dest\":\"tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":314,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"tb1qv4ak4l0w76qflk4uulavu22kxtaajnltkzxyq5\",\"id\":\"testID\",\"account\":0,\"ts\":123456}}", payload_4_.toJSON().toString());
    }

    protected BIP84Wallet computeBip84wallet(String seedWords, String passphrase) throws Exception {
        byte[] seed = hdWalletFactory.computeSeedFromWords(seedWords);
        HD_Wallet bip84w = hdWalletFactory.getBIP84(seed, passphrase, params);
        BIP84Wallet bip84Wallet = new BIP84Wallet(bip84w, params);
        return bip84Wallet;
    }
}
