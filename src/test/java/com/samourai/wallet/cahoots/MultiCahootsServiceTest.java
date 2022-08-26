package com.samourai.wallet.cahoots;

import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsContext;
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
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "7f3a7a5ead7cb4fa64a025e77241e93da2fb5ebe13d50d04d21ac7435e1a96c4";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc1200000000000016001440852bf6ea044204b826a182d1b75528364fd0bdfc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f020488130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014657b6afdeef6809fdabce7face295632fbd94feb0247304402201d95684e5b2be8b51956b577fe48610cad26b23017b07ab8397c6969a152993c0220030636a5a58180b52f08298a05362e723fc6e00cc1465c5db2f8c22bf971b101012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202483045022100bae0a1217265105bddfcd617f2947bbd48b16e403eb6d626d3d779c523db5af602206e712bf2223bff717ca2ecb86bd0ed1148c162f3459dabf648b981393cb3b3af012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_84[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[1], 4860L);
            outputs.put(SENDER_CHANGE_84[0], 4860L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "9fe891a71b46b85a0b54ed971c91ac908daa4c0ff8f5ee830d2b448637eecc8b";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff022f1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229632400000000000016001428a90fa3f4f285fc689f389115326dbf96917d620248304502210098e70691c23919fb9e344de6b110e8421aae50c57bdd825b486c655bbe7a3e53022034eae02622fe10d39672d472502fb83d5a74dd198553317054a79b12745aeda9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402204bc5ddb0ab788a8fff27b82d4132e0655fc48b11b52880674c2c9e8fe9a4d41202202e97b9d1348b5e11f97f4db2072981006936929bcc7175a70b8b2d8468e1bcec012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9315L);
            outputs.put(SENDER_CHANGE_84[1], 7471L);
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
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "9726981b1f7ed8a71b4c22ea9b3002cc8b200dd98e49cab99b9fec4af3015d5e";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fc12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273558813000000000000160014d6e3c19a583c95f3a7eb048b19747b14de5f5273024730440220515e0a3d6d7fc15baaad29673d32b91751218a16ce9a6440f830a43d0d43a4e90220524a82f930e79d568a698b7036f8955f9038450c0ff6e8ff94be8180a404c35c012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402202e849d1f4a1e0e7d4a5b630d0db107ae1629e63429570349c5dc45b337c3d84a022032ba8eb6721104883e9fe98e87104e68bb579f17e0835687c30ea4a2632ea0e9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(ADDRESS_XMANAGER, spendAmount); // counterparty mix: extracting to external
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4860L);
            outputs.put(SENDER_CHANGE_84[0], 4860L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "e4efedfaed35e9579421ef89d968076ed68013acd46e6e9e7700e7aea15faad4";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdfffffff960c7d2c49f7b413b619a049acf4a2d3df51859a6803e4b4f53cce010c064bc0100000000fdffffff022f1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229bb56c8200000000016001428a90fa3f4f285fc689f389115326dbf96917d6202483045022100fc092d322b7a70ec7caf1a126f757feaa3ed77d6f812465be3b5db495786d014022049bae6ef3b91940800d8eb92c602f43134fb350c4cf58563ef6ac3a071ca1baa012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402201c022aa97b8e9922fa7215794b28748a9ecd981038bdf5455ede6d893c8aa9670220155becad8f3e2935b91447cbb1f2458901177f4a1f13c07b56c3c45b946ad0c0012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 550000315L);
            outputs.put(SENDER_CHANGE_84[1], 7471L);
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
        MultiCahootsContext contextSender = MultiCahootsContext.newInitiator(account, feePerB, spendAmount, address, null);
        MultiCahootsContext contextCp = MultiCahootsContext.newCounterparty(account);

        Cahoots cahoots = doCahoots(cahootsWalletSender, cahootsWalletCounterparty, multiCahootsService, contextSender, contextCp, null);

        // verify stonewallx2
        {
            Transaction tx = ((MultiCahoots)cahoots).getStonewallTransaction();
            String txid = "f33fd67caea49faf2b737a6c16c52e312fd15490e9834f5eb964d0330583b29d";
            String raw = "02000000000102d54f4c6e366d8fc11b8630d4dd1536765ec8022bd3ab8a62fefc2ee96b9ccf140100000000fdffffffad05bb9c893f5cb9762ea57729efaf4a4b8eb1e377533fddc49d15d01fb307940100000000fdffffff04fc120000000000001600144e4fed51986dbaf322d2b36e690b8638fa0f0204fc12000000000000160014657b6afdeef6809fdabce7face295632fbd94feb88130000000000001976a9145f6f1ffd91751f52b20e0c0dad81daa6211d607688ac88130000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100b7eb6336e406fc1b16086b1b3f861b46debc66decd0668f020db5a9970580c74022039884e3bce22ac6e9649a14dadb418f742d02d6d40ad060edf660d0bcfc70c4f012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f202473044022039ae15503429dd2520a0163e6dc82406fc6d499cbf17c7bbc83191f9df85e3cc02206320ff5aa662597a185d476b99e20278db6571433ac8d17093f7b1e923af57ec012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(address, spendAmount);
            outputs.put(COUNTERPARTY_CHANGE_44[0], spendAmount); // counterparty mix
            outputs.put(COUNTERPARTY_CHANGE_84[0], 4860L);
            outputs.put(SENDER_CHANGE_84[0], 4860L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }

        // verify stowaway
        {
            Transaction tx = ((MultiCahoots)cahoots).getStowawayTransaction();
            String txid = "9fe891a71b46b85a0b54ed971c91ac908daa4c0ff8f5ee830d2b448637eecc8b";
            String raw = "020000000001023eb0ac773c2862f83ed98f1697af9be96bb3616b2877b0dce4a4f732772c1a7a0100000000fdffffff26de7fe4341de0b955ecaf4d2f232b7a9f47f8191959937c5372da46325cb6b40100000000fdffffff022f1d00000000000016001485963b79fea38b84ce818e5f29a5a115bd4c8229632400000000000016001428a90fa3f4f285fc689f389115326dbf96917d620248304502210098e70691c23919fb9e344de6b110e8421aae50c57bdd825b486c655bbe7a3e53022034eae02622fe10d39672d472502fb83d5a74dd198553317054a79b12745aeda9012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f20247304402204bc5ddb0ab788a8fff27b82d4132e0655fc48b11b52880674c2c9e8fe9a4d41202202e97b9d1348b5e11f97f4db2072981006936929bcc7175a70b8b2d8468e1bcec012102e37648435c60dcd181b3d41d50857ba5b5abebe279429aa76558f6653f1658f200000000";
            Map<String,Long> outputs = new LinkedHashMap<>();
            outputs.put(COUNTERPARTY_RECEIVE_84[0], 9315L);
            outputs.put(SENDER_CHANGE_84[1], 7471L);
            verifyTx(tx, txid, raw, outputs);
            pushTx.assertTx(txid, raw);
        }
    }
}
