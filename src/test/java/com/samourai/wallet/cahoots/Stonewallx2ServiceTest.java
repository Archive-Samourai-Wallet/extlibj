package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.Stonewallx2Context;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Stonewallx2ServiceTest extends AbstractCahootsTest {
    private static final Logger log = LoggerFactory.getLogger(Stonewallx2ServiceTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void STONEWALLx2_BIP84() throws Exception {
        int account = 0;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "27f8b3b8fc5d71b244960734928bde9a443d5f13f7bc59cc5e033fea2f7e63a7";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100de92c5fcc7544df67ee9a24ad119b0472808b87bcc9bede9743d80308a9aaf3b022070415c276c4591e6da9f2c6474889c8e260dbd05829d5e6148a6970d6751c673012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203c15b77345a8a78a7f442261ef3293ad5ab8f407bcba034dc8504e112798f11502206a0da3a7004c9bea437171b556868f55c4d1b6a5cd99f32ed9587e05736aa20b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4858L);
        outputs.put(SENDER_CHANGE_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_P2TR() throws Exception {
        int account = 0;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_P2TR;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "228e5b4bce1b5a68f4e7229bba002383fd16560c40dd37056a6f861b66dff54a";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04f51200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdf5120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433024730440220107316b715c1e76c93d66e8f9afea66775badcca6aa1a3ee0e1bdd20e1804a2902202e570d5085b4baff382030f978c2f2dba4c26762f8c90e49228c16e25361c1ef012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e3861ddb61e5f3fded878f21778bdc33a1c01d198c968c512bad7c5e13b45b9102203ec7e86b9b49a2688b43bd96af3992179ddd66e029839af3d1e389fe4ec4914c012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4853L);
        outputs.put(SENDER_CHANGE_84[0], 4853L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 294, 147, 0, spendAmount, 4853, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_BIP84_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "8144f20e912d398e7b5075babb38404b4f4a019fc70d35beb03c297aefcbcd6e";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5fa12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735588130000000000001600148f7446b625c8f1365c42f5e0b17b33f1a2b27ae402483045022100ee5f3a27272eab261543c8ddbd434527b4f0bf31f70302e7efd32c612fca444b0220626ff79fb729bdfb3196390d623025deef836b513500535d1393e2ede4cce427012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100cf44dec6fca5bfcfa1852769fffb71f5e57db1808e61c8cffb427b807f52dde602200ef684250678c25d7065b75fd83ba56df71322298ba89a5283d516d5fa4567fe012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4858L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_BIP44() throws Exception {
        int account = 0;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "24a4e042a2ffb763048f7f6a68311edb9caf33de6f71da8b0fcb167547400653";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac0248304502210091c2bf145a8c1d6f32a2508fba1b22dbca9d3ef8be2ed8d3ff2fc93ac296b56c0220578824be641b1fb2f75d52dc3ad844c899bff286b314f88c5fd1f86763bb0df5012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100f2ce9acba2b7e6d79289fcda5005f6bcc58b409108349bc8b29cbdc034fcd80702200ef1b224019498d1c7e6b412fe9a8fde2822fc69a9746b3b886f9936900e13b2012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4858L);
        outputs.put(SENDER_CHANGE_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_BIP44_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "9613a62f1af95973ace29348fe0293da3208cc678dbbd537167833d550d24b3f";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5fa12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001976a9148f7446b625c8f1365c42f5e0b17b33f1a2b27ae488ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100b62a7feff4fdac7490488c9b4bdfc088a7dd70ed6fe9e293a6f1109cf3092b2a022031569b6c3cda3a34d7ac367e2b250cff7dd4148c59d9701a0d69fb977d0589a8012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e83acf63d0dd89889b1f741db7461e4e76aa94ee77a9288e8d4207ac6baf1d4c022078257f2aa804dd21924bd80116fb7098b18b9c799efc8db971332202c25d8be7012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4858L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_BIP49() throws Exception {
        int account = 0;

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP49;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "5e64335e5d702249925f7f86ee02e3308fe9f6ba10a746287ab4dca488094c14";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fa12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287881300000000000017a9149f0432151dfca9ed873a3dab5106f470181be85c8702483045022100a7d58cd29c143f4d3b3349de2207b709a940cd1b4ea909afa23a77cee1eb434202205e55a8a0eef3c8b1dd0006638e22f49c8c172289214bb8c46f8a1d12c43c89c6012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022042b989d33d585810484b1a8ae6d88a49d51c03727f39b200ab1f0824e66ed8c202203ba8c4e3bae14c38c7cf2947984a8c700b36fb2826f02eced53edde03f734663012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_49[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4858L);
        outputs.put(SENDER_CHANGE_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void STONEWALLx2_BIP84_paynym() throws Exception {
        int account = 0;
        String paynymDestination = "TESTPAYNYM";

        // setup wallets
        UTXO utxoSender1 = utxoProviderSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        utxoProviderCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, account, FEE_PER_B, spendAmount, address, paynymDestination);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, account);

        Cahoots cahoots = doCahoots(stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "27f8b3b8fc5d71b244960734928bde9a443d5f13f7bc59cc5e033fea2f7e63a7";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000ffffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000ffffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100de92c5fcc7544df67ee9a24ad119b0472808b87bcc9bede9743d80308a9aaf3b022070415c276c4591e6da9f2c6474889c8e260dbd05829d5e6148a6970d6751c673012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402203c15b77345a8a78a7f442261ef3293ad5ab8f407bcba034dc8504e112798f11502206a0da3a7004c9bea437171b556868f55c4d1b6a5cd99f32ed9587e05736aa20b012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4858L);
        outputs.put(SENDER_CHANGE_84[0], 4858L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify paynym
        Assertions.assertEquals(paynymDestination, cahoots.getPaynymDestination());

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 284, 142, 0, spendAmount, 4858, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void invalidStonewallExcetion() throws Exception {
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";

        // throw Exception for 0 spend amount
        Assertions.assertThrows(Exception.class,
                () -> {
                    Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, 0, FEE_PER_B, 0, address, null);
                    stonewallx2Service.startInitiator(cahootsContextSender);
                });

        // throw Exception for blank address
        Assertions.assertThrows(Exception.class,
                () -> {
                    Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, 0, FEE_PER_B, 0, "", null);
                    stonewallx2Service.startInitiator(cahootsContextSender);
                });
    }
}
