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
        String txid = "99dbd7d2105e12a0b5e776cb24c34d17ca5f56d7ec02ef9769363cb85bf92dd7";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdeb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100b6ae253086c05e94c5c94c5f95d63dd8cb300aadc98c6673567b38ca97034ea1022079952a1a592491e3aac85971fcc893370a7566681bd7d9034b11a02f5e1e1112012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100d893ece1aa4b85b7b8105b8475bc7ddcee4884d138323886694cac839b5df83f02200218714ea8939e8e918e144dfe732b828543c028b00f39ce7f7300bfea285dfb012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
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
        String txid = "ce22e1eb3b78ea0cfa8f97c311488ea06e27acae0dc6e172eec5dd7622db9201";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5eb12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735588130000000000001600148f7446b625c8f1365c42f5e0b17b33f1a2b27ae402473044022002faa86010e6e05c9d0eb152207bc64f5a4813da753e6b831b8053eda6304a46022028c39294c62e0372be738cb096791ea4ffab003262f4afa25e88dcb36f065edf012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100977d8c7f34ddd8832bd2c204950eb6f1013b96a7b21e083420a1caaba4837b5c02206d6a8f90136471ca4eb8872e83d2007f29608f2f85fa9950fb621bd922965c5f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4843L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4843L);
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
        String txid = "57fa4a8a07e54be4000ec822686a17344a8788808d78faa387be8490cdcb0855";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100dbc69f8e7f9f1069c60fb460fba7f2ddb48e824bea65e3955308215459b2dedd0220382b3f91fe7b8a9078e009c282715799b7731ae474923e3ca9f0439438680596012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e6d9200d5d54a53559a2faba6f8d82d8857a6bf8f4a21962174825a3b3615c2702203ad9fc8a6a924a08fa674b809198cf4b29cfda6b1fa158df739597b71ced9bb9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
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
        String txid = "43cfb2cdcb723bb38a415e662b510a7eccbfda11c380ee7bc4785bd08bce4b9b";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600146a4f3d067dad1077d7414a442ddc5554f0ddc2d5eb12000000000000160014c92e53e44ae5d9d392aecf1ce7a980b073b01cd688130000000000001976a9148f7446b625c8f1365c42f5e0b17b33f1a2b27ae488ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100821972323a406a9872a86df8fae2ea95ed78cec8d6909dafe291489980db2246022054f8d77a90f9be9dd3000396a170fcce8318dec4ad2ba7c62781fb6dbce5574a012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402206fa3037b3ec2b4458715135f6edb31efa573a166c2f4cbff5b1e3407c7d77b7b0220364a96e5a732ef396c37e9f0230706a22639e2434edc86b79b58f510fd5e4237012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_44[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_POSTMIX_84[1], 4843L);
        outputs.put(SENDER_CHANGE_POSTMIX_84[0], 4843L);
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
        String txid = "b192549465898eafefc5a1b4c7e9b500f10e83ff3f7288881fc0cc6eb50b0467";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb881300000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287881300000000000017a9149f0432151dfca9ed873a3dab5106f470181be85c8702483045022100b7ac52d767ca80ae40723de4a74bbc980cdea318d61651e67ad4f3398ef65fe102204b37f6f2da2cafdc45e3cb94406a9adb42c758dbcccfe330d09d3f97471f9e54012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402201666c8d3a9ee4d482a581d35800a5249a834cbaa30dfdf89b0c974690ff9e5f702202593923850d3a6b0ba8d0841fcde853de08c3618c8f417d630ca79b8f83747c6012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_49[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
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
        String txid = "99dbd7d2105e12a0b5e776cb24c34d17ca5f56d7ec02ef9769363cb85bf92dd7";
        String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdeb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100b6ae253086c05e94c5c94c5f95d63dd8cb300aadc98c6673567b38ca97034ea1022079952a1a592491e3aac85971fcc893370a7566681bd7d9034b11a02f5e1e1112012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100d893ece1aa4b85b7b8105b8475bc7ddcee4884d138323886694cac839b5df83f02200218714ea8939e8e918e144dfe732b828543c028b00f39ce7f7300bfea285dfb012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(address, spendAmount);
        outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
        outputs.put(COUNTERPARTY_CHANGE_84[1], 4843L);
        outputs.put(SENDER_CHANGE_84[0], 4843L);
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
