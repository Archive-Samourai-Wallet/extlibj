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
        String txid = "e5c6df03befcf1e70382066d2011186383d53c7d9a48f13f5f16f2557d858252";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b6120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100d3e7e7ed9c42631ea56730ddf04e7f4d08d3e79c54c6b4a09482c801ccf5717002200cce706f9a5bfec189d75c8cd3e7d87ba6bdc8841e34c7ebd68029981730a91f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e95efdf8247859d5f59299f70631e8c9154dcec58fac670064b9f7252acfe1c002204ead98bf5a21d16e06a5dc97f7573d025c7b314506079d63fb5f4296daab32af012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_84[0], 4790L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STOWAWAY, Arrays.asList(utxoSender1), 210, 210, 0, spendAmount, 4790L, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void Stowaway_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        StowawayContext cahootsContextSender = StowawayContext.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount);
        StowawayContext cahootsContextCp = StowawayContext.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stowawayService, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "c655eed464d24741d4c78bd780e415522a8614466ff878b82665d76704f79558";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff02b612000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd6983a00000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100ddc5a6c14fa2c8aa0c55d957e65ca3b1e2c3142d04732245db5e9811e2e8ceec02203ae9e35fce7543e19ef702b8b0ba36073da105e7cac0e59a8d5f5add7d201754012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100dd40147a0338f4a9234a44c1532119b0b61574254ac9c3d79a957f892289af430220180878b6f180226eff62f06fbb901cc2ace28ec5ba5431c1424f64db8e787ee8012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(COUNTERPARTY_RECEIVE_84[0], 15000L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4790L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STOWAWAY, Arrays.asList(utxoSender1), 210, 210, 0, spendAmount, 4790L, BIP_FORMAT.SEGWIT_NATIVE);
    }
}
