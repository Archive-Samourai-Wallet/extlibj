package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(cahootsWalletSender, account, feePerB, spendAmount, address);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "99dbd7d2105e12a0b5e776cb24c34d17ca5f56d7ec02ef9769363cb85bf92dd7";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdeb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02483045022100b6ae253086c05e94c5c94c5f95d63dd8cb300aadc98c6673567b38ca97034ea1022079952a1a592491e3aac85971fcc893370a7566681bd7d9034b11a02f5e1e1112012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100d893ece1aa4b85b7b8105b8475bc7ddcee4884d138323886694cac839b5df83f02200218714ea8939e8e918e144dfe732b828543c028b00f39ce7f7300bfea285dfb012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4843L);
            outputs.put(SENDER_CHANGE_84[0], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "90f25c8860b4ee87023f4cb7c5c69148ec2f7163362a548216f5c817b58e65b4";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02fc1c00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229742400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100f8ecaad321a05a39f32406beb78c609f4568a1663ab644ec6b5868d09c64f7fc022069d4c14981b25598d2fbe184425127f235a74946426c83166d1723604f9e6e7d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100d9c2283ff49f65f017e60165b3e0caef2cc46ee7c29d1691324c1c1353a6c19d0220479bfd4ea5dbcee8e7909d0709987c72fa266a79d92bffdaf6b97c5a00a2ebf1012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9332L);
            outputs.put(SENDER_CHANGE_84[1], 7420L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_bip84_extract() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        // counterparty > THRESHOLD
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx3", 1, 550000000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP84;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(cahootsWalletSender, account, feePerB, spendAmount, address);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "3ce4b60b8ef3b1e1be3f6efda43002d1429dd4db26250e77c40132d25ee94f6f";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f5273024730440220722cf6edce2308831992584e61f65a004bca42f97aaf2bf5fc75f2433f796cd10220751cd27d74e2cb6107e224000e83ccd2858d2359a9af65d0d72a4c3e72614035012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022038e9cd11ed4a0085e797f5a192f707f56f54646a8bafeb2ddfa1dbfcb42405a1022019c4e89b89abdf4632d9ded2c47e35d4daa14831e9b3141669e444fad9a9eb6d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(ADDRESS_XMANAGER, spendAmount); // counterparty mix: extracting to external
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
            outputs.put(SENDER_CHANGE_84[0], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "7ddad59e5275fc10bb0e62eabd8ef3c32618acfc59f327918525b6d063665bd3";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdfffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000fdffffff02fc1c00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229cc56c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202473044022044fb1cb66b01d4fc364bd4b7185f9cc8aaf1b3eb8f7e00e64e120d035d32a147022027a4bbf626b7feabdf4e85d0291a3567de78e7607907ab9f996f9abb78d43747012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100da52356f89a519c194623d4e37a15399e8db6e8e381d0459fae6a53325dddf7f02207f137a50ec91636380851798ec44772066a05e13ffde40365f41231e151bd787012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 550000332L);
            outputs.put(SENDER_CHANGE_84[1], 7420L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }

    @Test
    public void multiCahoots_bip44() throws Exception {
        int account = 0;

        // setup wallets
        cahootsWalletSender.addUtxo(account, "senderTx1", 1, 10000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");
        cahootsWalletSender.addUtxo(account, "senderTx2", 1, 8000, "tb1qkymumss6zj0rxy9l3v5vqxqwwffy8jjsyhrkrg");

        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx1", 1, 10000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");
        cahootsWalletCounterparty.addUtxo(account, "counterpartyTx2", 1, 9000, "tb1qh287jqsh6mkpqmd8euumyfam00fkr78qhrdnde");

        // setup Cahoots
        long feePerB = 1;
        long spendAmount = 5000;
        String address = ADDRESS_BIP44;
        CahootsContext contextSender = CahootsContext.newInitiatorMultiCahoots(cahootsWalletSender, account, feePerB, spendAmount, address);
        CahootsContext contextCp = CahootsContext.newCounterpartyMultiCahoots(cahootsWalletCounterparty, account, xManagerClient);

        Cahoots cahoots = doCahoots(multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "57fa4a8a07e54be4000ec822686a17344a8788808d78faa387be8490cdcb0855";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04eb120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204eb12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100dbc69f8e7f9f1069c60fb460fba7f2ddb48e824bea65e3955308215459b2dedd0220382b3f91fe7b8a9078e009c282715799b7731ae474923e3ca9f0439438680596012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100e6d9200d5d54a53559a2faba6f8d82d8857a6bf8f4a21962174825a3b3615c2702203ad9fc8a6a924a08fa674b809198cf4b29cfda6b1fa158df739597b71ced9bb9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4843L);
            outputs.put(SENDER_CHANGE_84[0], 4843L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "90f25c8860b4ee87023f4cb7c5c69148ec2f7163362a548216f5c817b58e65b4";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02fc1c00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229742400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100f8ecaad321a05a39f32406beb78c609f4568a1663ab644ec6b5868d09c64f7fc022069d4c14981b25598d2fbe184425127f235a74946426c83166d1723604f9e6e7d012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100d9c2283ff49f65f017e60165b3e0caef2cc46ee7c29d1691324c1c1353a6c19d0220479bfd4ea5dbcee8e7909d0709987c72fa266a79d92bffdaf6b97c5a00a2ebf1012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9332L);
            outputs.put(SENDER_CHANGE_84[1], 7420L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
