package com.samourai.wallet.cahoots;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultiCahootsServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsServiceTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void multiCahoots_bip84() throws Exception {
        int account = 0;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(cahootsWalletSender, account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "27f8b3b8fc5d71b244960734928bde9a443d5f13f7bc59cc5e033fea2f7e63a7";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100de92c5fcc7544df67ee9a24ad119b0472808b87bcc9bede9743d80308a9aaf3b022070415c276c4591e6da9f2c6474889c8e260dbd05829d5e6148a6970d6751c673012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203c15b77345a8a78a7f442261ef3293ad5ab8f407bcba034dc8504e112798f11502206a0da3a7004c9bea437171b556868f55c4d1b6a5cd99f32ed9587e05736aa20b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4858L);
            outputs.put(SENDER_CHANGE_84[0], 4858L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "c04a519b673967e9c24f27f02e6a8c290a3e749d023283e2e99d3602ad782273";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000ffffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000ffffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229652400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100d24db8719fba4e1ea3bb6533703cc7c75822ab8585bb3ec5e0b20e9534b475a6022003fdf41f43f240c3dd1057750372767863fb49b291dd859fe9efe106edaf5e9b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f0dbbe937c6b2386c7568a6ace0dba9aa04453e774c5a0fc273f3c94d366bf4702201d23167e35658eb3ffc5b0f9691c98495133888f2ebbad3f97a7a3d37b60d1e3012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9317L);
            outputs.put(SENDER_CHANGE_84[1], 7467L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2 as SpendTx
        SpendTx spendTx = cahoots.getSpendTx(contextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858L, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void multiCahoots_bip84_extract() throws Exception {
        int account = 0;

        // setup wallets
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        // counterparty > THRESHOLD
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx3", 1, 550000000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(cahootsWalletSender, account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "c8d65493767e1e3420d7757947ab043feb0ba5939bab93f6830660e1cfb4732d";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f52730247304402200aeaff46afd4704b495821027d05038e6f7249069e02af7e3e3a45a52db5c880022014c31a6e0a6d715e14e798cb54c904dd624e87e895890662658f7b22fc0d639a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100a5581e42b9285dc6705c4ef7adde401f6eb7d3afa604ab7fcc1364529459d5e5022015f96b81e486913c4a32863b615bacf26d7a74ad74949d2fb449e0135d6ce324012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(ADDRESS_XMANAGER, spendAmount); // counterparty mix: extracting to external
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4858L);
            outputs.put(SENDER_CHANGE_84[0], 4858L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "2086c6aba43f618ba3fcc104c26e92e4032c099db49d00d6eaf82569693bcc87";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fffffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000ffffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229bd56c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100c643d3c14e766bfb1bdbb6e01c9a40cda375fbd629715e34a1b742b864baf096022049f5fa519057be6b852d29725fc14b376b32a9ec7be99307ee9cd181c0507715012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203f33f48360a582f26796abae81660893bb9da06a080b8ef7b4a6a616584600890220387c2227449c4a9304d1fb1eec44fec112c37fd7b7d408c904dee6613700f8c8012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 550000317L);
            outputs.put(SENDER_CHANGE_84[1], 7467L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_bip44() throws Exception {
        int account = 0;

        // setup wallets
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(cahootsWalletSender, account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "24a4e042a2ffb763048f7f6a68311edb9caf33de6f71da8b0fcb167547400653";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac0248304502210091c2bf145a8c1d6f32a2508fba1b22dbca9d3ef8be2ed8d3ff2fc93ac296b56c0220578824be641b1fb2f75d52dc3ad844c899bff286b314f88c5fd1f86763bb0df5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f2ce9acba2b7e6d79289fcda5005f6bcc58b409108349bc8b29cbdc034fcd80702200ef1b224019498d1c7e6b412fe9a8fde2822fc69a9746b3b886f9936900e13b2012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4858L);
            outputs.put(SENDER_CHANGE_84[0], 4858L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "c04a519b673967e9c24f27f02e6a8c290a3e749d023283e2e99d3602ad782273";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000ffffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000ffffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229652400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100d24db8719fba4e1ea3bb6533703cc7c75822ab8585bb3ec5e0b20e9534b475a6022003fdf41f43f240c3dd1057750372767863fb49b291dd859fe9efe106edaf5e9b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f0dbbe937c6b2386c7568a6ace0dba9aa04453e774c5a0fc273f3c94d366bf4702201d23167e35658eb3ffc5b0f9691c98495133888f2ebbad3f97a7a3d37b60d1e3012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9317L);
            outputs.put(SENDER_CHANGE_84[1], 7467L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_P2TR() throws Exception {
        int account = 0;

        // setup wallets
        utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_P2TR;
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(cahootsWalletSender, account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "228e5b4bce1b5a68f4e7229bba002383fd16560c40dd37056a6f861b66dff54a";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04f51200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdf5120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433024730440220107316b715c1e76c93d66e8f9afea66775badcca6aa1a3ee0e1bdd20e1804a2902202e570d5085b4baff382030f978c2f2dba4c26762f8c90e49228c16e25361c1ef012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e3861ddb61e5f3fded878f21778bdc33a1c01d198c968c512bad7c5e13b45b9102203ec7e86b9b49a2688b43bd96af3992179ddd66e029839af3d1e389fe4ec4914c012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4853L);
            outputs.put(SENDER_CHANGE_84[0], 4853L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "c6e4d6ffd6a7d0a833696807326052fa091118ff1b23421b267f7c3d7ffcb6f1";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000ffffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000ffffffff02261d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c82296a2400000000000016001428a90fa3f4f285fc689f389115326dbf96917d62024830450221009885c598b48ca6fc97e829f94ee7c5ee65e3bd385222a8d36c254a601978e51002207dd5381c9b5504bcf204096212698da3d4b431cac2037480b2c36ff11f3dea73012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203e423038513e4fa8e23220dd0b46a80a21277cee6550621b0a555df6a27244cf02207ccffda9e7ac5d9b400bc2e90d3df77254854a070a57aa57f064fac202b21127012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9322L);
            outputs.put(SENDER_CHANGE_84[1], 7462L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
