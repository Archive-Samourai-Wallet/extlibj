package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

    private MultiCahootsService stowawayService = new MultiCahootsService(bipFormatSupplier, params);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {    }

    @Test
    public void Stowaway() throws Exception {
        int account = 0;

        final String[] EXPECTED_PAYLOADS = {
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"0SSi2009eKpFtT:zi8HLHa}ck6/Hkw:o0)vTbpx=@4vkHOx?(I00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@Rse00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP800kXBmfjRjX+BN2pndzp<)fi].wO#(&8FW]3$Xn%w>tyOis^V<0W6:Ri!<h[F&KK!1N.gopnE1+?WPY:b1X1%<8HY{7&6c}0][C{lTj3O^sFTN9z?DpRm0w&&^0>VR!607wJ{@j[=eT&00\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"0SSi2009eKpFtT:zi8HLHa}ck6/Hkw:o0)vTbpx=@4vkHOx?(I00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@Rse00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.j}j0W5{PRE$EoQ.Vqx?Bj5LlEaYo}NU!&VKj2YCv!w=gDtHVf$*EpcKaLarYHEDUR+$vjsh$<e)-UtN0Gm-E>yu+0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbenh9ElaPP7E^%K]:{C7^}Vui@3Pu(B{&O+hR?u4pXm3xRwepNZzkZ+XFOTC6&6V*HCI:Qso<}uLY0-g7jLV3KYPrx8E0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009eKpFtT:zi8HLHa}ck6/Hkw:o0)vTbpx=@4vkHOx?(I00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@Rse00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.j}j0W5{PRE$EoQ.Vqx?Bj5LlEaYo}NU!&VKj2YCv!w=gDtHVf$*EpcKaLarYHEDUR+$vjsh$<e)-UtN0Gm-E>yu+0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbenh9ElaPP7E^%K]:{C7^}Vui@3Pu(B{&O+hR?u4pXm3xRwepNZzkZ+XFOTC6&6V*HCI:Qso<}uLY0-g7jLV3KYPrx8E0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"f0d70870\",\"step\":5,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009eKpFtT:zi8HLHa}ck6/Hkw:o0)vTbpx=@4vkHOx?(I00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@Rse00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.j}j0W5{PRE$EoQ.Vqx?Bj5LlEaYo}NU!&VKj2YCv!w=gDtHVf$*EpcKaLarYHEDUR+$vjsh$<e)-UtN0Gm-E>yu+0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbenh9ElaPP7E^%K]:{C7^}Vui@3Pu(B{&O+hR?u4pXm3xRwepNZzkZ+XFOTC6&6V*HCI:Qso<}uLY0-g7jLV3KYPrx8E0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"eed8a1cd\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"f0d70870\",\"step\":6,\"collabChange\":\"tb1qsktrk7075w9cfn5p3e0jnfdpzk75eq3f5qced4\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}"
        };

        final HD_Wallet bip84WalletSender = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_INITIATOR);
        TestCahootsWallet cahootsWalletSender = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletSender), bipFormatSupplier, params);
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        final HD_Wallet bip84WalletCounterparty = TestUtil.computeBip84wallet(SEED_WORDS, SEED_PASSPHRASE_COUNTERPARTY);
        TestCahootsWallet cahootsWalletCounterparty = new TestCahootsWallet(new WalletSupplierImpl(indexHandlerSupplier, bip84WalletCounterparty), bipFormatSupplier, params);
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // sender => doStowaway0
        long spendAmount = 5000;
        MultiCahoots payload0 = stowawayService.startInitiator(cahootsWalletSender, "tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3", spendAmount, account); //grabbed random addr from testnet
        verify(EXPECTED_PAYLOADS[0], payload0);

        // receiver => doStowaway1
        MultiCahoots payload1 = stowawayService.startCollaborator(cahootsWalletCounterparty, account, payload0);
        verify(EXPECTED_PAYLOADS[1], payload1);

        // sender => doStowaway2
        MultiCahoots payload2 = stowawayService.reply(cahootsWalletSender, payload1);
        verify(EXPECTED_PAYLOADS[2], payload2);

        // receiver => doStowaway3
        MultiCahoots payload3 = stowawayService.reply(cahootsWalletCounterparty, payload2);
        verify(EXPECTED_PAYLOADS[3], payload3);

        // sender => doStowaway4
        MultiCahoots payload4 = stowawayService.reply(cahootsWalletSender, payload3);
        verify(EXPECTED_PAYLOADS[4], payload4);

        // receiver => doStonewall0
        MultiCahoots payload5 = stowawayService.reply(cahootsWalletCounterparty, payload4);
        verify(EXPECTED_PAYLOADS[5], payload5);

        // sender => doStonewall1
        MultiCahoots payload6 = stowawayService.reply(cahootsWalletSender, payload5);
        verify(EXPECTED_PAYLOADS[6], payload6);

        // receiver => doStonewall2
        MultiCahoots payload7 = stowawayService.reply(cahootsWalletCounterparty, payload6);
        verify(EXPECTED_PAYLOADS[6], payload7);
    }
}
