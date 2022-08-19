package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
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
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStowaway(account, FEE_PER_B, spendAmount);
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "f14c1d6fab6e9217aa5d6d5af951f6287a75cbb0567f87e8ed77c99aa9f0b1f5";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0290120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d620247304402204455b847d7dab36f2dd55f214cfd3b6d59fba4f5d1d6bda50b3518d77c09591902207f2cd102830b886a5ff0baa63adebff63cc48bf033d04ffdd0119c3b8b65c5ef012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e3d10f18644199a3c66c8740469a4a537635a099ca9e61804f5a3c12620858ff022011c9cb215a97822f16fd7818f155c25bb133f7bb314472f78ca56f94cf4e4657012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4752L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void Stowaway_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        CahootsContext cahootsContextSender = CahootsContext.newInitiatorStowaway(account, FEE_PER_B, spendAmount);
        CahootsContext cahootsContextCp = CahootsContext.newCounterpartyStonewallx2(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "e1e25ead2d9202addb0d678c2c114560587f1202b0e6b5d21f8c10a860709ea1";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff029012000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd6983a0000000000001600143bdff7532977f4f8094cfa8ccff2574cc71eebe702473044022017167cfdfb40864da9bc40b2cfd65fc3c4bca8ac388c9c8a6ca757e3dc013f2902206bb427ff871452e2f89f6ce5957c61b45725db6eb7742b1c36d69fc1b6612fd5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402202d2dc89c9881d4273dfd5877382998ca758cb32590a59f947f895257b84abdf0022078dc9c251b14376560728d3daa5204d1a59a3f00275628750d84b05c396ef04e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_POSTMIX_84[0], 15000L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4752L);
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
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, FEE_PER_B, 0, address);
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });

        // throw Exception for blank address
        Assertions.assertThrows(Exception.class,
                () -> {
                    CahootsContext cahootsContextSender = CahootsContext.newInitiatorStonewallx2(0, FEE_PER_B, 0, "");
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });
    }
}
