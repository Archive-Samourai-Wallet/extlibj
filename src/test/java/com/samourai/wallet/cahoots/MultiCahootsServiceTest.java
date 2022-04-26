package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.RandomUtil;
import com.samourai.wallet.util.TestUtil;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

/*
NOTICE: This may sometimes fail because of the multiple UTXOs. The service can occasionally choose at random the UTXOs under certain criteria. This is normal.
You may need to run it a couple of times until it selects the right UTXOs. I don't really want to remove the randomness, and it's not too high of a priority to manually set a seed here.
 */
public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

    private MultiCahootsService stowawayService = new MultiCahootsService(bipFormatSupplier, params);

    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE_INITIATOR = "initiator";
    private static final String SEED_PASSPHRASE_COUNTERPARTY = "counterparty";

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void Stowaway() throws Exception {
        int account = 0;

        final String[] EXPECTED_PAYLOADS = {
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":0,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":1,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":2,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP800kUAl&[F/{5#bxV4$pP+uUAhzl^b#vb&hP-N:5Ou[loe^m/G2aprHN4!?Ymws^v.]wW4cL2/u[P!<Q0x$-B6(7v?uVJhiJ<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(00000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":3,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":5000,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.t1l0WdM25WH9vmj/(Y8bdh%f!GjG)R=(FK9tA97vq@2}{Ba0aq{nLNDagZJBgVXvfxi+Py0&!Y/dUnMGoCrm[xD/x-PvU<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(0.j}j0W6t[1RXLNH=c9[t)Su8Vq=Y?&#rD^/5E0yd@@kGoPf2}3vEZv{C4InO[iDz&<4d0r>[E7gSc>tZ2e?NPzE#Z0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":50,\"outpoints\":[{\"value\":8000,\"outpoint\":\"7a1a2c7732f7a4e4dcb077286b61b36be99baf97168fd93ef862283c77acb03e-1\"},{\"value\":10000,\"outpoint\":\"9407b31fd0159dc4dd3f5377e3b18e4b4aafef2977a52e76b95c3f899cbb05ad-1\"}],\"type\":2,\"dest\":\"tb1q9z5slgl572zlc6yl8zg32vndh7tfzltzz3pw8w\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":4,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":50}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.t1l0WdM25WH9vmj/(Y8bdh%f!GjG)R=(FK9tA97vq@2}{Ba0aq{nLNDagZJBgVXvfxi+Py0&!Y/dUnMGoCrm[xD/x-PvU<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(0.j}j0W6t[1RXLNH=c9[t)Su8Vq=Y?&#rD^/5E0yd@@kGoPf2}3vEZv{C4InO[iDz&<4d0r>[E7gSc>tZ2e?NPzE#Z0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":5,\"collabChange\":\"\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.t1l0WdM25WH9vmj/(Y8bdh%f!GjG)R=(FK9tA97vq@2}{Ba0aq{nLNDagZJBgVXvfxi+Py0&!Y/dUnMGoCrm[xD/x-PvU<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(0.j}j0W6t[1RXLNH=c9[t)Su8Vq=Y?&#rD^/5E0yd@@kGoPf2}3vEZv{C4InO[iDz&<4d0r>[E7gSc>tZ2e?NPzE#Z0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":0,\"fingerprint\":\"eed8a1cd\",\"step\":6,\"collabChange\":\"tb1qsktrk7075w9cfn5p3e0jnfdpzk75eq3f5qced4\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}",
                "{\"stonewall_tx\":\"\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.t1l0WdM25WH9vmj/(Y8bdh%f!GjG)R=(FK9tA97vq@2}{Ba0aq{nLNDagZJBgVXvfxi+Py0&!Y/dUnMGoCrm[xD/x-PvU<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(0.j}j0W6t[1RXLNH=c9[t)Su8Vq=Y?&#rD^/5E0yd@@kGoPf2}3vEZv{C4InO[iDz&<4d0r>[E7gSc>tZ2e?NPzE#Z0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":7,\"collabChange\":\"tb1qsktrk7075w9cfn5p3e0jnfdpzk75eq3f5qced4\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}",
                "{\"stonewall_tx\":\"0SSi2009eKpFtT:zi8HLHa}ck6/Hkw:o0)vTbpx=@4vkHOx?(I00000@R#]$cF<mkg-+I+rQu[-fc.#[PgwU]8cJeJq/ZOYgf?n{0rr910r8@$@@:OT00000000=m6S/%m-iqCX/fu38NLV+A-*+nb5!2AN0000002tLGG{B/C@?Cy8=vI0.dw-ZMY?B2bH.x:*000007624W^DDtY3*5AhbBe2eC@wei7[&ae6951j0000m02j=v[<Z3nmb]Ztq.%[0hIDcA4kE[+nh9ElaPP*I0X)yOWF]KRpa9L9e(B#m}%{=MN]Iezh^cnG+}o5uE+L9Jn86((LIA0?86vCRkC?refFbm}Y+OH>(iEk}0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"stonewall_amount\":0,\"stowaway_tx\":\"0SSi2009c+U=J4hc$q%n/]D]nUBB=(VTER$CD/(-R1VS*ee&*n00000@R#]$TP=tbI9CmQB$PRndETDjoocpqCt*-Z-gmT&af#ip0rr910r8@$@@J@S00000000=m6I.D>qj8ji]chR?zFoGkiqKc!1vsEo0000002tLGd5^-L]ZW.lxRdnD6/6TvMxuP80.t1l0WdM25WH9vmj/(Y8bdh%f!GjG)R=(FK9tA97vq@2}{Ba0aq{nLNDagZJBgVXvfxi+Py0&!Y/dUnMGoCrm[xD/x-PvU<90%XtWVvmFWr&Dp>:m)Wx5SMC%W%$wMXUqknEw(0.j}j0W6t[1RXLNH=c9[t)Su8Vq=Y?&#rD^/5E0yd@@kGoPf2}3vEZv{C4InO[iDz&<4d0r>[E7gSc>tZ2e?NPzE#Z0u?K8C1s#9vbfdXV^Z0(G)PV7TlX+<lyK/-sOuq<7fHbc0000\",\"cahoots\":{\"fingerprint_collab\":\"f0d70870\",\"psbt\":\"\",\"cpty_account\":0,\"spend_amount\":5000,\"outpoints\":[{\"value\":9000,\"outpoint\":\"b4b65c3246da72537c93591919f8479f7a2b232f4dafec55b9e01d34e47fde26-1\"},{\"value\":10000,\"outpoint\":\"14cf9c6be92efcfe628aabd32b02c85e763615ddd430861bc18f6d366e4c4fd5-1\"}],\"type\":2,\"dest\":\"\",\"params\":\"testnet\",\"version\":2,\"fee_amount\":248,\"fingerprint\":\"eed8a1cd\",\"step\":8,\"collabChange\":\"tb1qsktrk7075w9cfn5p3e0jnfdpzk75eq3f5qced4\",\"id\":\"testID\",\"account\":0,\"ts\":123456},\"stonewall_destination\":\"tb1qas00y34404zwx2fp2veekveks4c5crfjx802v3\",\"stowaway_fee\":0}"
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
        verify(EXPECTED_PAYLOADS[7], payload7);

        // receiver => doStonewall2
        MultiCahoots payload8 = stowawayService.reply(cahootsWalletCounterparty, payload7);
        verify(EXPECTED_PAYLOADS[8], payload8);
    }
}
