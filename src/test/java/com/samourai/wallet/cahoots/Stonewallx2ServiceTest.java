package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.Stonewallx2Context;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "7f3a7a5ead7cb4fa64a025e77241e93da2fb5ebe13d50d04d21ac7435e1a96c4";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb0247304402201d95684e5b2be8b51956b577fe48610cad26b23017b07ab8397c6969a152993c0220030636a5a58180b52f08298a05362e723fc6e00cc1465c5db2f8c22bf971b101012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100bae0a1217265105bddfcd617f2947bbd48b16e403eb6d626d3d779c523db5af602206e712bf2223bff717ca2ecb86bd0ed1148c162f3459dabf648b981393cb3b3af012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4860L);
        outputs.put(SENDER_CHANGE_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_P2TR() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_P2TR;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "dc4b56a2e9a107c0083cb22fb0bf6d61399c5715977adef99033c7dc6b740550";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04f71200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdf7120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433024830450221008b3289086d657fbcba55e803df06f0b120ef3af5a3808a98a6ee556f9307e3f702201a6cb1987f1ad0e45c6f17670008eda3ebc8120a7ed37d395c2004e65b583c19012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100cc088ecb5870f25148b0f8eb8a797de8273c66f84371d6922f907d391b6f1aa9022049b3d7fc91c57587495126c73ca94be6e32bd71cdc818158223094a8eef5acbc012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4855L);
        outputs.put(SENDER_CHANGE_84[0], 4855L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP84_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "a0a28649a2db8bac993bd7edc79d3eb790e7e2067367ea41f5beebf30e4b706e";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5fc12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735588130000000000001600148f7446b625c8f1365c42f5e0b17b33f1a2b27ae402483045022100efd5df99ca3697bba6b77ae3847e8c4d8ae521f3d39943c719b08d3fe9b274f9022076ae6d4d41a04602d8c59767bc85f6b4d936736086a44ad3db7433863a13c3a9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e1a5b1a8da4c11ab2de01e697967b1085faf06d0ae313fcc3d5d59e1bfde0e9702202c83cb04a957a05306185b6f6b9e12c9c5e9e6d5014fd98d188d88b2fdfefb71012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4860L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP44() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "f33fd67caea49faf2b737a6c16c52e312fd15490e9834f5eb964d0330583b29d";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fc12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100b7eb6336e406fc1b16086b1b3f861b46debc66decd0668f020db5a9970580c74022039884e3bce22ac6e9649a14dadb418f742d02d6d40ad060edf660d0bcfc70c4f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022039ae15503429dd2520a0163e6dc82406fc6d499cbf17c7bbc83191f9df85e3cc02206320ff5aa662597a185d476b99e20278db6571433ac8d17093f7b1e923af57ec012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4860L);
        outputs.put(SENDER_CHANGE_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP44_POSTMIX() throws Exception {
        int account = SamouraiAccountIndex.POSTMIX;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "a4074469ec28199f3eaa300457bd5b97cdde8a673262e0f2672da6c3ac9260b2";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5fc12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001976a9148f7446b625c8f1365c42f5e0b17b33f1a2b27ae488ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac024830450221009cf528f086b3be82445413cea0b86952dcafdb5c47a777dceb4f8aa43a9d65170220337dc3312e5d48bd321263e2e04b6d3899425d89c31bb15427104ab293f50730012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024830450221009d8e722d87d78a833524f19f1e507b741df86b95bb3b8b3294d135a594008962022012552eebc2c206e169dda72e22d68f3aff4ec54707b28c071cb7a6a1957d8e09012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4860L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP49() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP49;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, null);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "14df67966ad2b298d6de2c6f71beb43b545da7a219810769e589835196dc076a";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fc12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287881300000000000017a9149f0432151dfca9ed873a3dab5106f470181be85c8702483045022100d5ddd9e69752bb0071082195b790b153eb02e7e4afca910c3ea0cb1494bcc6ca0220276c72153918d9ee859e39d13541241394381239f149f712bfc9a49e8866d4e1012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022040b13ed26028f6b408c81c4ff67b03ed7385d25d0f7e74d33780b13455b489c902206bdfaf7ed647eba21430fd926d0675ad10e5deddab61be34083b1b4f494bd616012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_49[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4860L);
        outputs.put(SENDER_CHANGE_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);
    }

    @Test
    public void STONEWALLx2_BIP84_paynym() throws Exception {
        int account = 0;
        String paynymDestination = "TESTPAYNYM";

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(account, FEE_PER_B, spendAmount, address, paynymDestination);
        Stonewallx2Context cahootsContextCp = Stonewallx2Context.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, stonewallx2Service, cahootsContextSender, cahootsContextCp, null);

        // verify TX
        String txid = "7f3a7a5ead7cb4fa64a025e77241e93da2fb5ebe13d50d04d21ac7435e1a96c4";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb0247304402201d95684e5b2be8b51956b577fe48610cad26b23017b07ab8397c6969a152993c0220030636a5a58180b52f08298a05362e723fc6e00cc1465c5db2f8c22bf971b101012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100bae0a1217265105bddfcd617f2947bbd48b16e403eb6d626d3d779c523db5af602206e712bf2223bff717ca2ecb86bd0ed1148c162f3459dabf648b981393cb3b3af012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4860L);
        outputs.put(SENDER_CHANGE_84[0], 4860L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify paynym
        Assertions.assertEquals(paynymDestination, cahoots.getPaynymDestination());
    }

    @Test
    public void invalidStonewallExcetion() throws Exception {
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";

        // throw Exception for 0 spend amount
        Assertions.assertThrows(Exception.class,
                () -> {
                    Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(0, FEE_PER_B, 0, address, null);
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });

        // throw Exception for blank address
        Assertions.assertThrows(Exception.class,
                () -> {
                    Stonewallx2Context cahootsContextSender = Stonewallx2Context.newInitiator(0, FEE_PER_B, 0, "", null);
                    stonewallx2Service.startInitiator(cahootsWalletSender, cahootsContextSender);
                });
    }
}
