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
            String txid = "7455f15769b0f876bf3853cf20ef60090255bffea5dd26e5a521eb2dd0b88faf";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04001300000000000016001440852bf6ea044204b826a182d1b75528364fd0bd00130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb02473044022079a012b8bc5197e8c0ff1750f91432d78cbb6e742f76e512be05739ec648525402206316231492a7b4789c93487d08d04d1417688dec863315e3a59cf97533e552b9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100df600bb360487f1f2b48438c77f85978c96353794357fca8de8d3c59898dcf7a022076f284c4bc7142ee94d536e36ffea126481c3aa48899ff7853d7dd69e05b3880012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4864L);
            outputs.put(SENDER_CHANGE_84[0], 4864L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "2e782010171546ebafeb6d412b224fdf410d257bcc2227bdcd75e2784533543d";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02371d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c82295f2400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100ea9f18b8d262e90b525909c214a24c20783874799a749d92262436f1bc889df702205d16e831df6f6b0da7d000ab93d0fc8d4759d63dbec9f0afc2a98ff362855d21012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024730440220364714c1fd2390638c1f73e8815f582ad4c7529658ccffd3c6292af9cc8dbd2002205ae693c62616db54f505e5ec1d590b4956da2c3af44b7213a00c90d958d5d420012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9311L);
            outputs.put(SENDER_CHANGE_84[1], 7479L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stonewallx2 as SpendTx
        SpendTx spendTx = cahoots.getSpendTx(contextSender, utxoProviderSender);
        verifySpendTx(spendTx, SpendType.CAHOOTS_STONEWALL2X, Arrays.asList(utxoSender1), 272, 136, 0, spendAmount, 4864L, BIP_FORMAT.SEGWIT_NATIVE);
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
            String txid = "6d5d3c2e2bf869968369de552e7977a1d02efe8e4417748f8561a6de4551dcdf";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02040013000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f527302483045022100e60cc59650a3198db5f078a69ace49660cdacf080ff1c573771089e9fbca1767022036c95f5f9c095ff3748adc2c384537d5fd3b5fa3dd4b3e4a9656041623c03761012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20248304502210092946459da1c8f50b6dd18e79d447437362e9872fefe6807836a6fb1de6d0e4102203daf521a2e6f6edf9c99437f32a3f5211b27e6ed14e47f504b7baa872bef7b0c012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(ADDRESS_XMANAGER, spendAmount); // counterparty mix: extracting to external
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4864L);
            outputs.put(SENDER_CHANGE_84[0], 4864L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "2b2f214a3254a07a86380e195c4f9b886c8efddd5baf7d65675ea4ad06b9adb0";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdfffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000fdffffff02371d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229b756c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100989c45eb4add509051ea3b44c0d2450ccfa9a14a4765a436aab3f5584af604fb022024251a83727c390b205138b808b2eb42b11366dac0e8c577fdf6562858094609012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20248304502210096263beaf841302b1872ae271c14f8ad6c1788af20aa0acfcff1ebf7c8755598022032489aa8abbe72881f19179fa6d14a198d4433ce97477f5c48e439406d0d66d4012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 550000311L);
            outputs.put(SENDER_CHANGE_84[1], 7479L);
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
            String txid = "a774b3a2ae1f8e9d3efa7e893fa4cf1bdb7ce2086598ecb0cfa389f3e7ffaca5";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff0400130000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02040013000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100a87c9ad18ced31c742da41b20e6d6bffae920a253f54eafeb6ee4747116f7972022032712d71d84620edb9256144cc74f0d851b31139be50a9c52dac0aba8b8be19e012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20246304302203627ee5828f15a41ddbba9f312570988598cf2ab3f6caf02542aaa1d40ddbaf3021f2ce45f15b0073462ca461fd2beae6bd8904a7a430d379496348af77388f1ba012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4864L);
            outputs.put(SENDER_CHANGE_84[0], 4864L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "2e782010171546ebafeb6d412b224fdf410d257bcc2227bdcd75e2784533543d";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02371d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c82295f2400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100ea9f18b8d262e90b525909c214a24c20783874799a749d92262436f1bc889df702205d16e831df6f6b0da7d000ab93d0fc8d4759d63dbec9f0afc2a98ff362855d21012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024730440220364714c1fd2390638c1f73e8815f582ad4c7529658ccffd3c6292af9cc8dbd2002205ae693c62616db54f505e5ec1d590b4956da2c3af44b7213a00c90d958d5d420012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9311L);
            outputs.put(SENDER_CHANGE_84[1], 7479L);
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
            String txid = "d062b854a61e1e2c5470f1517590f1b5b0b9b701ce6a5918f1c1305575d4037e";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fa1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfa120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f02048813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb8813000000000000225120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e864330247304402203d89de72a2e2f245c7b8d69f7f7086b852e7852ff930fb1380f61d981eb1fd9102201ebf70e1344f4466a8b5a36ce78f598eeef503a5abf38d055c3055a2ca4ac339012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022001be1c4334b889dd8a617d830989e84313fd84fb69c1b2a180547f5c502b21d00220740f53d64f9bc15349785ae4ba2aa5d2cf423f6543ae261eb014e4af8fcfc570012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2d2040000";
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
            String txid = "cc5d2a32001aff632899997bb0f356e71748987b8dd139f7b9a303fe5bd3468f";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff02311d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229652400000000000016001428a90fa3f4f285fc689f389115326dbf96917d6202473044022013a4b42d7bd7b95fd2d92011e9c8f3eeffc67f630155f9577fd356b0e268704602204939ff02eb5072dad8165649c02726bdfd6581e9a3f4e3053f15f82818d99d30012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f2024730440220755eec82506c7df81acf464c35b7d2fd843e9d6e58a117f0b57b91ce92e0c7d402207dd231e93c975fd783cc23cb2e89a4ac545a97a38dfb53f47e24789cffe852c7012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9317L);
            outputs.put(SENDER_CHANGE_84[1], 7473L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
