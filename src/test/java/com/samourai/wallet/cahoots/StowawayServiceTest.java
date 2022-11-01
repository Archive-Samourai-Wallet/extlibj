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
        boolean rbfOptin = false;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(account, FEE_PER_B, spendAmount, rbfOptin);
        StowawayContext cahootsContextCp = StowawayContext.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "5ee43707672e0fdf27c87e398c6f547f33dadef43fa510c2b7e22ab5fc271b85";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b0120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100e1507e5b457b5052488d41c1685bab3f2eb5ef51fc515168d3bbb14c30adf9cf02203c463ceb0394d68a3e4b037fcff91eec0e04e9b7357eb3479d07fa3a6e93ed7e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203ab7cba03ee40b4f4fceb02c5d2735be560dab25eb264e6850b307be3350383d022046a4a23ed073d13a7792a4dbfa22a46ad123b7cabb31fa8e9aa0cdcf1318f6e1012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4784L);
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
        boolean rbfOptin = false;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(account, FEE_PER_B, spendAmount, rbfOptin);
        StowawayContext cahootsContextCp = StowawayContext.newCounterpartyMulti(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "fb121061765e4f6179e6be262253427e4c22d7b006207cc84c62d77bf26ee923";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b012000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd6983a0000000000001600143bdff7532977f4f8094cfa8ccff2574cc71eebe702483045022100af62a5e82e112f82f2fcfcae0461de195902e2bd378d26f9f236834d7fac2d0d0220481aeb526c294b8718347be5195c147f334a6b4863f8508ca8ea30fcd5b0ff30012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100cc9caebc56646e70b4983291d6315f0ebbbd21953a8c1801dc1f14cebd7ead630220567e18db1380ac1c4e7153ffce5b38f3684decccbb00a6c2a64b455fb6766595012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_POSTMIX_84[0], 15000L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4784L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }
}
