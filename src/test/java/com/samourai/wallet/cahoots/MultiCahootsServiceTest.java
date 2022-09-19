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
            String txid = "44ad31cb9cd68283bc746ccf8b88e02a2a617fb8f45aba0e0f35f4eaa5c5686b";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb024730440220041c9f5e3810d77aee65d6fab99d325b67a8f8435548eed48d2809779b5da1ea02204819c0fc77ae2e4bbb091effbccd7e7987adf4feac04a9ab5751d0f72260cea5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df91fdcffa6dd5e7139a319b8ac5beec98e32ad1625073596654acc9d2ea6cad02202c607842ff05652814e963627a47d2e3d819c8f8772f362d2b6bda4a84a5aeab012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "fd918da2c2fb00b2aa071fa0e12afd8f9daad4427babe261d96ed35946f3c237";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229652400000000000016001428a90fa3f4f285fc689f389115326dbf96917d620247304402201f4ccc9f8ae4b6202d18fe9bf1f1f7847528b9d618c6daceba87d6400c09716c0220012b04f2a2860c4e58e1928c890f5ad3658fb10694b18265cfb1f6fcc42c9270012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402205612ca04146d2b10e6184c4a72bb20c645529d2e2cf88ab546516ecda8aa25eb02203dce007a19c3d93b7339331666ca683e5d7eab097360ed8f9bf2d8de70d31e6e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9317L);
            outputs.put(SENDER_CHANGE_84[1], 7467L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2 as SpendTx
        SpendTx spendTx = cahoots.getSpendTx(contextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, spendAmount, 0, BIP_FORMAT.SEGWIT_NATIVE);
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
            String txid = "3611050bcc7a56027d9b8b8d0efcc2b3bccfd752e2c4a54d84e7e405fff29a17";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f5273024830450221009cafa865643af9d178f1addb071d6df171f9e44340b93fc92cf343853b8a44c7022058366170c91d4a2d86c1fbee673879bd7200a8096057e902b63171fc895e598b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022037efbe3a76eede0897bf1c72b729da5b9250e53d448fd376729a17ca7b39ebe00220060d5d7f6343140b8cd673eb5e181e3cc8539a6a15984d479b6d158d66988417012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "c7bda99a9d4f863518dbdd43253100e95de9dbb775cd38b1ff573033d8f04307";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdfffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000fdffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229bd56c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100977b318e52cc44f95198171895d150cbef7b2b6042710b1428185d84766eaa0f0220660ed9150aa0974794a4aa3147e9f207ae31ab8b14e02af3855f8578dcb3b5e4012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100c82a579a98b91ce8fc7a3b51691640d1aa5ab44d170282997899abb8290c632a0220243cf433b830d9025771d6437f66f746b6d452e607411c4966cd03b41ca33eb2012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "586652ea8c2b899eaaf9f4ab7075a80be9e063d52df4ff89da80e1b9e011ae13";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac0248304502210085a2b34de125d5a1f26ccc25528624ac2404cb72b0df750ea81e6506daf25c6c022001ab0aa043f9406cfd8c86746a9a6ce9b31d1b917a9e7dc3b06a7220615a513b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402201f879e60473c7140a66f9a7e8afba37ec6e1775f6808c384f6220a42f7b7bdcb02203d8a74d017287b8a6dbb479813ccc24a2879ddf781305671dfcc48ec5f3f796d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "fd918da2c2fb00b2aa071fa0e12afd8f9daad4427babe261d96ed35946f3c237";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff022b1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229652400000000000016001428a90fa3f4f285fc689f389115326dbf96917d620247304402201f4ccc9f8ae4b6202d18fe9bf1f1f7847528b9d618c6daceba87d6400c09716c0220012b04f2a2860c4e58e1928c890f5ad3658fb10694b18265cfb1f6fcc42c9270012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402205612ca04146d2b10e6184c4a72bb20c645529d2e2cf88ab546516ecda8aa25eb02203dce007a19c3d93b7339331666ca683e5d7eab097360ed8f9bf2d8de70d31e6e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "0d2798c1070b6413cbe7f3f04a0581ef4e137844ab3df6ac4f3978ebb2e99a6f";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04f51200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdf5120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433024830450221008a48ee43553781c30ab77d2d6be08c956b184ca421b0d2cffc4a08df5cfc3ade02204f46312bd1a18a4b6e7db1b37657d6a43e765b6484d99d5daa16a1be691171b0012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20248304502210080dab11746ef92269a03e7db1b8f7593a49872ebff225996c93566a7ddcd0e9d02202feba7d4c544a8d7cc7d12e03d733d2def8d626618ed168ca597fbc3414344c8012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
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
            String txid = "63e461ea8a63d6b2ad02f52c37b8495ded53afa2e1d8b1667fd699cb9dc4ce3c";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02261d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c82296a2400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100b4082773cadfbfe55815728afed88716849883e6ceda7c2d262617529e8f19080220455743ee930bbc27e93ae8c5819f30d323cebbe986147fdb469e93832aa7a7ee012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022074006e629040646e0b8ee0ec68168794a2820cd69a2e68de9c8586d91aa58ed302200fc5e5d90469c4626fd541ffc176b96ca8d67f2c3c9dbc44054b71477564c546012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9322L);
            outputs.put(SENDER_CHANGE_84[1], 7462L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
