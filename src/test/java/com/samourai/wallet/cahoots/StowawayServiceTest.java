package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.StowawayContext;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class StowawayServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(StowawayServiceTest.class);


    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void Stowaway() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(account, FEE_PER_B, spendAmount);
        StowawayContext cahootsContextCp = StowawayContext.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "68288513bc233a9cba0ec25b9905978594abe9d58c08b4dd343b62735cb796a0";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b2120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100cc5c9e084dd91109876133fa72ebed55407617a21853d9b05af7d96bdbe6616d022062ddf2ee0b44c3062a490dde0738ec21a87b182da12d4291a13a8c86c9033d9a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022061bd1c152021eb9c8ed6d51a76333ac26b43fc1ba6544fdd95d779955e1af89402200b9b0c98e62aef73a877e977d09a33c52e9a661ea2bf4d2aa3c4c51113d1000f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4786L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void Stowaway_MULTI_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(account, FEE_PER_B, spendAmount);
        StowawayContext cahootsContextCp = StowawayContext.newCounterpartyMulti(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "6bf8505a55315c50e5a7d8d4f88be002585307e689da486eb2fa86e08ac4e7d4";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b212000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd6983a0000000000001600143bdff7532977f4f8094cfa8ccff2574cc71eebe702483045022100b1e33a8b31c8babca206a22b0514b6df812cad7453c4075da06ddcc14e8451ae02205d53aeaabfbaf67e209b4e65ad824899c9c5ca7bbfba7f563c053b625890fe7d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022076cceac9d0ac555b32a077ee3a54831b13b32d36d5071bbfd282ab44ab86ab0002202932c130b0cfa2d36a8c282ab10a60e9a66d88b45540f6555d773c56af9f961a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_POSTMIX_84[0], 15000L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4786L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }
}
