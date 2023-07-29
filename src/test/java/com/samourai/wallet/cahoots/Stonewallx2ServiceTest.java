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
        String txid = "7455f15769b0f876bf3853cf20ef60090255bffea5dd26e5a521eb2dd0b88faf";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04001300000000000016001440852bf6ea044204b826a182d1b75528364fd0bd00130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02473044022079a012b8bc5197e8c0ff1750f91432d78cbb6e742f76e512be05739ec648525402206316231492a7b4789c93487d08d04d1417688dec863315e3a59cf97533e552b9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df600bb360487f1f2b48438c77f85978c96353794357fca8de8d3c59898dcf7a022076f284c4bc7142ee94d536e36ffea126481c3aa48899ff7853d7dd69e05b3880012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4864L);
        outputs.put(SENDER_CHANGE_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
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
        String txid = "d062b854a61e1e2c5470f1517590f1b5b0b9b701ce6a5918f1c1305575d4037e";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e864330247304402203d89de72a2e2f245c7b8d69f7f7086b852e7852ff930fb1380f61d981eb1fd9102201ebf70e1344f4466a8b5a36ce78f598eeef503a5abf38d055c3055a2ca4ac339012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022001be1c4334b889dd8a617d830989e84313fd84fb69c1b2a180547f5c502b21d00220740f53d64f9bc15349785ae4ba2aa5d2cf423f6543ae261eb014e4af8fcfc570012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

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
        String txid = "619748af8371d3f070d8bc8bc7a7d1e7895fbafbb840e2e2aed93205b620c318";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d50013000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735588130000000000001600148f7446b625c8f1365c42f5e0b17b33f1a2b27ae402483045022100b7230d40d9b1b6ebc01f5775c9a0c102575de70221debbec69ad539f08e5b6d50220200ccef48d2e54229fd828894d520ea7a4c19e10f6059dc2cec43836a18c0426012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024830450221009e1e08086408f1fa8f9e5bb027f366c766597f899e155dfee7c8b9588cb0105802204a2a373f1ef44749fc5c4e70e150a24fc0ef9cc2abbd7899ca7bda90b39f938d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4864L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
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
        String txid = "a774b3a2ae1f8e9d3efa7e893fa4cf1bdb7ce2086598ecb0cfa389f3e7ffaca5";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02040013000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100a87c9ad18ced31c742da41b20e6d6bffae920a253f54eafeb6ee4747116f7972022032712d71d84620edb9256144cc74f0d851b31139be50a9c52dac0aba8b8be19e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20246304302203627ee5828f15a41ddbba9f312570988598cf2ab3f6caf02542aaa1d40ddbaf3021f2ce45f15b0073462ca461fd2beae6bd8904a7a430d379496348af77388f1ba012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4864L);
        outputs.put(SENDER_CHANGE_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
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
        String txid = "f0ee149ebdb38a1b729942805147ab85be028451c85fc23496e9466b227270dc";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d50013000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001976a9148f7446b625c8f1365c42f5e0b17b33f1a2b27ae488ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02473044022077164aa46487e34245815af5133b12e327345c984e54d5a5c7231d1c84ea20cc0220244d3d5ab51b91e293b0af73cc2828fdbb498c12b416069a8fc8fd79fa9f5627012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e9886ec97ac203461f4105437a79dc5862af86acf10b18e744dfe1f07cd985540220724789d0c3fbc76aa20762cf2769f9cf8cfb33c8c8efeb265a1b308ca790505d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4864L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
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
        String txid = "8abfdd674d249222bd7939b157b946fd208f99f78be80e274ef7a93a6aa92987";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02040013000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287881300000000000017a9149f0432151dfca9ed873a3dab5106f470181be85c8702483045022100e37ac41d6083bebfa40445efb5b5354e6186a34b3881eba001469acfb808b67002203b96406657f8ff9a8eea3af4a36b00435d3d5ae86a9d9acd7dd8c3b028b14020012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402207654c2ec707142880f923fb995f94a5d90f4a162955f610a9e983db27f00dd9b0220114554ad73fee664e1713640f6f73548e24379d6ffded45873aee8b838d203ca012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_49[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4864L);
        outputs.put(SENDER_CHANGE_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
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
        String txid = "7455f15769b0f876bf3853cf20ef60090255bffea5dd26e5a521eb2dd0b88faf";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04001300000000000016001440852bf6ea044204b826a182d1b75528364fd0bd00130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02473044022079a012b8bc5197e8c0ff1750f91432d78cbb6e742f76e512be05739ec648525402206316231492a7b4789c93487d08d04d1417688dec863315e3a59cf97533e552b9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df600bb360487f1f2b48438c77f85978c96353794357fca8de8d3c59898dcf7a022076f284c4bc7142ee94d536e36ffea126481c3aa48899ff7853d7dd69e05b3880012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4864L);
        outputs.put(SENDER_CHANGE_84[0], 4864L);
        verifyTx(cahoots.getTransaction(), txid, raw, outputs);
        pushTx.assertTx(txid, raw);

        // verify paynym
        Assertions.assertEquals(paynymDestination, cahoots.getPaynymDestination());

        // verify SpendTx
        SpendTx spendTx = cahoots.getSpendTx(cahootsContextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864, BIP_FORMAT.SEGWIT_NATIVE);
    }

    @Test
    public void invalidStonewallExcetion() throws Exception {
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
