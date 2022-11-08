package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.StowawayContext;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount);
        StowawayContext cahootsContextCp = StowawayContext.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "84ac538e370ce0490a8af3f618ed5017135fc1ae186a5790ce1e81e918640cf5";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff02b0120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202473044022072c3e454ddec2a324219b499f286e025b1a40bf0cc2881a87f76c841b0164d51022021d2cfbb1add09465403f326941b74b2d5e10ef75e10dd985c21ca12640cd79d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024830450221009f5605a5263a54f253d084dcd30eddf163dd8c1e25e98116b5bd1cd9a09ae12b02200c0cdfa2cdf647c258319f809f4a984e8c77df9387d8e57e8a25d0d08beb16b8012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4784L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STOWAWAY, Arrays.asList(utxoSender1), 216, 216, 0, spendAmount, 4784L, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void Stowaway_MULTI_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount);
        StowawayContext cahootsContextCp = StowawayContext.newCounterpartyMulti(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "c40a4043f06612d93b38ba4cac684a19dda657ab9f2766549009c6f9828bc3e3";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff02b012000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd6983a0000000000001600143bdff7532977f4f8094cfa8ccff2574cc71eebe70247304402204ba2f3a1d6d33099c3d611b1e320034a4f8e72a244ba00f77243f60c104f31c902202e267a59f4d0d088b867d39f06d7ae2ffce2231360eaad0198d8bd45019cc2cd012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024730440220758050240e8c1f3386405ef3042a3a17a7fd244c23119beb633a8ae65eef8a160220200a1475582a04f5a269633934a237cb5524aed71623aee76bef7c82fb7f3c3f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_POSTMIX_84[0], 15000L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4784L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STOWAWAY, Arrays.asList(utxoSender1), 216, 216, 0, spendAmount, 4784L, BIP_FORMAT.SEGWIT_NATIVE);
    }
}
