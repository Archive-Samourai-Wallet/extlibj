package com.samourai.wallet.send.spend;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendError;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendTxSimple;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.wallet.send.exceptions.SpendException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

public class SpendBuilderTest extends AbstractSpendTest {
    private SpendBuilder spendBuilder;

    private static final long BLOCK_HEIGHT = 12345678;

    public SpendBuilderTest() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        spendBuilder = new SpendBuilder(utxoProvider);
    }

    private SpendTx spend(String address, long amount, boolean stonewall) throws Exception {
        // spend
        BigInteger feePerKb = BigInteger.valueOf(50);
        BipFormat forcedChangeFormat = null;
        List<MyTransactionOutPoint> preselectedInputs = null;
        return spendBuilder.preview(depositWallet84, depositWallet84, address, amount, stonewall, true, feePerKb, forcedChangeFormat, preselectedInputs, BLOCK_HEIGHT);
    }

    @Test
    public void simpleSpend_noBalance() throws Exception {
        long amount = 10000;

        // no utxo

        // spend
        try {
            spend(ADDRESS_BIP84, amount, true);
            Assertions.assertTrue(false);
        } catch (SpendException e) {
            Assertions.assertEquals(SpendError.INSUFFICIENT_FUNDS, e.getSpendError());
        }
    }

    @Test
    public void simpleSpend_insufficient() throws Exception {
        long amount = 10000;

        // set utxos
        utxoProvider.addUtxo(depositWallet84, 9000);

        // spend
        try {
            spend(ADDRESS_BIP84, amount, true);
            Assertions.assertTrue(false);
        } catch (SpendException e) {
            Assertions.assertEquals(SpendError.INSUFFICIENT_FUNDS, e.getSpendError());
        }
    }

    @Test
    public void simpleSpend_single_bip84() throws Exception {
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend(ADDRESS_BIP84, amount, true);
        long changeExpected = 9852;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 148, 148, 0, amount, false, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "809a999f4d524bec6d41b384ebb37e5f225a031c472cdd0e033f8ddbf6c30802",
                "010000000001010f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffff027c26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016410270000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502473044022066a4f7c50e58cbf2f8af9c76f5dbd7e617d5998968309dca5c08d5ec2aa7a04702203656035e097b9d1e7c60623c1add35750a31a72bb30d014f9a9105fa87d969100121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
    }

    @Test
    public void simpleSpend_single_bip49() throws Exception {
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);
        long changeExpected = 9852;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 148, 148, 0, amount, false, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "05da048eb69c919bc2aba509f690e3c4b681476d8fa1ccda04c6695d522c4ebb",
                "010000000001010f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffff027c2600000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87102700000000000017a914336caa13e08b96080a32b5d818d59b4ab3b367428702483045022100b7ff4ddacb148c9180475438bf637976b860c0855215c079f16bdc7c7ea7ea2f0220613fe70f49ddb9ce9597d2680b37c30fff9d0a93df7a238a63fd56aa89849e640121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
    }

    @Test
    public void simpleSpend_2utxos() throws Exception {
        long amount = 40000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 500);

        // should select largest Utxos
        SpendTx spendTx = spend(ADDRESS_BIP84, amount, true);
        long changeExpected = 9780;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 220, 220, 0, amount, false, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "18d346d1e03b289fecebff2286dbe86724cf3f75283a1daac85b1d0da5d5353f",
                "010000000001020f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffff4a2c8e9e650ec859ca8ca1f63da4791284c8e09c8eed9d0e641962c8fe38f4e60300000000fdffffff023426000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d127355024830450221008eced796f84229f59eecc39b8ec24f2f70de2068e311efea5cd6f0d7798b52e8022064a144b1a3b613d7aac2aed1435da3749f26b5afeab641e26f98fefbf18de0710121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402473044022021a9270630a179ccd7eb046965c73a7892f9159b1b0aa3f9558155d82f09a2fb02203f048b1f1429e86e687ac7601111b1ada85d15dbc61fcbe96aabb77f4b12ccc30121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc00000000");
    }

    @Test
    public void simpleSpend_3utxos() throws Exception {
        long amount = 50000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 500);

        // should select largest Utxos
        SpendTx spendTx = spend(ADDRESS_BIP84, amount, true);
        long changeExpected = 9708;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 292, 292, 0, amount, false, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "2920ad00ce526f8ff1f064dfff3e992c5492d9f1f665e8b1371396288e6aad99",
                "010000000001030f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffffcbaf7f91af27d64b157eeeaee086a25a0057bff334e491f1b9e9a66a4c7b002b0100000000fdffffff4a2c8e9e650ec859ca8ca1f63da4791284c8e09c8eed9d0e641962c8fe38f4e60300000000fdffffff02ec25000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016450c30000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d127355024830450221008a3c3c184b364320fbaa46c1b180d294029d1e73298cc9beb1cb0a8804c6266602205d75a68986ac237c0f33a373dfdfe4b70bcbadd930c2b349841bc951e4fe9ceb0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100f7aef953e7e63b42827ca95efaf777b03b03fbbdc690ac3b70420be6408f4cf60220056c2c2049936fea99a8549f211e7e3283dafc27c8315807f2cf566727d8244201210229950f82d3230b06db77c9aec65180e5bbf99fd837fe967dbabe4d9b0422146c024730440220073248a99dc698a1423196318b46c6d1902fdbd80a2940519fc796f47c07480e022009de030234b667cb6ac84a0e24c26275d94a3e77e47e280bf9ec441894786d500121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc00000000");
    }

    @Test
    public void stonewall_bip84() throws Exception {
        long amount = 40000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo5 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo6 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo7 = utxoProvider.addUtxo(depositWallet84, 40000);
        UTXO utxo8 = utxoProvider.addUtxo(depositWallet84, 40000);

        // should select STONEWALL
        SpendTx spendTx = spend(ADDRESS_BIP84, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], amount);
        outputs.put(ADDRESS_CHANGE_84[2], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo6, utxo5), 502, 502, 0, amount, false, 69498, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "cb62a58b9169f7a13f770b85236d27a3d45fe44ce6e3d59e488f9820ac16679c",
                "020000000001050f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffffb2b62d26c5d91df9ac9b3d26a86762966379df322f645b1a2cbe9f95655622380400000000fdffffff710eee3a480151e5b479bd0d1952dca0397c0c82e1da65d906606c5dd7779e5a0600000000fdffffff9bfd729fa0f2fce42b6a4346f7218eeaa624ce346e4cc8c1b2dd8a318a06686a0500000000fdffffff4a2c8e9e650ec859ca8ca1f63da4791284c8e09c8eed9d0e641962c8fe38f4e60300000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600144910e17f5ca698222657369753a164262605087a409c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100dd5be40f3153c16f70815ce82be75ff434284f75ef49496f49ab12b00cee9b760220186e10be15633b8efd3679fe805145c1ced8dd213b7194848d05bfec7c542d640121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a64024830450221009b72509756d79c20f33011151529928d76620f1e4eff2d567638c244eaefd0700220040d7978966f3930a78e5dfd60531c937374c72f48459aaf414cf21cca6c628c012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd860247304402207519c225eac7d772d7a618d1b1a2968e1e3b3f6447eca97e4708e45eadfb47950220477bff0855abadb959d22535d6033de5e6c8b0f69ddbc92f2b75988c68e7fbf2012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100aa69500deee81835313c93fde812ed62fb31dd2f78db3df200bbd320ed8ffeb8022016d93fac593b027fddb835a57de08f95ee172e7307d5b1605d142ba8c0b6c949012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec3024730440220586d44038304d1d547e6d6b2f096c5b11494499ae67b438d1f6c1c5544a27d4d02201eef038a333ed0eadf06ec416955b518864d0ccf0138a64b76a9a7f64ed9af8d0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc4e61bc00");
    }

    @Test
    public void stonewall_bip49() throws Exception {
        long amount = 40000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo5 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo6 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo7 = utxoProvider.addUtxo(depositWallet84, 40000);
        UTXO utxo8 = utxoProvider.addUtxo(depositWallet84, 40000);

        // should select STONEWALL
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo6, utxo5), 502, 502, 0, amount,false,  69498, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "c389412260428090c6e1398fd67a0f2058be9c606bec5b841045e760718e69d6",
                "020000000001050f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffffb2b62d26c5d91df9ac9b3d26a86762966379df322f645b1a2cbe9f95655622380400000000fdffffff710eee3a480151e5b479bd0d1952dca0397c0c82e1da65d906606c5dd7779e5a0600000000fdffffff9bfd729fa0f2fce42b6a4346f7218eeaa624ce346e4cc8c1b2dd8a318a06686a0500000000fdffffff4a2c8e9e650ec859ca8ca1f63da4791284c8e09c8eed9d0e641962c8fe38f4e60300000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c00000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87409c00000000000017a914336caa13e08b96080a32b5d818d59b4ab3b36742870247304402200c724230b79d83c0a55b358fa69b725f340d1876c84b7536ee058318371f099d022057a78e48ec5cb441de90d9b3d278a143e1cebc79607b78f01cdcae34abd9d5470121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100cba7cee7b2349fe046227a648d015a823284706417b4e864e3c5c772a2f3bf900220150c4c19dbf65ac6040f19a7bf5f44f0d323037a8fb6a23ba1f5a26924b84d81012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd860247304402202cb6b2e53e0124a84f15b98a7b7ab6bc6e97a9d589ef282e081c80a0905cc04402205a99dca9e8efccec72ddd234e72d45414d026e8d4edc25609033879cfdd7cba6012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100957e7d37496eeb98950383ce8729b5ba91cb0a78fda57068426bff15651dc4be022023ddbff82e5735dc08589c68f6d7e51a2b1196bf6db396b2169c55378beab2d1012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100fb16d35aad84185a07bdfa785065cc8eac6570a1722cdcb519d52122fc2709e602200984bf886ddbc8f916ca2b40f1238b7472255b4a57d1cf63f66754814ca8bcb40121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc4e61bc00");
    }

    @Test
    public void stonewall_bip44() throws Exception {
        long amount = 40000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo5 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo6 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo7 = utxoProvider.addUtxo(depositWallet84, 40000);
        UTXO utxo8 = utxoProvider.addUtxo(depositWallet84, 40000);

        // should select STONEWALL
        SpendTx spendTx = spend(ADDRESS_BIP44, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP44, amount);
        outputs.put(ADDRESS_CHANGE_44[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo6, utxo5), 502, 502, 0, amount, false, 69498, BIP_FORMAT.LEGACY,
                outputs,
                "f93354e7ddd8143400cc29299854fec185b60ead4c2457c20b078aeaf5787da3",
                "020000000001050f934535839dd32f98a041695796f68c43b1e087f3ab68d1380d915ed5dd130d0200000000fdffffffb2b62d26c5d91df9ac9b3d26a86762966379df322f645b1a2cbe9f95655622380400000000fdffffff710eee3a480151e5b479bd0d1952dca0397c0c82e1da65d906606c5dd7779e5a0600000000fdffffff9bfd729fa0f2fce42b6a4346f7218eeaa624ce346e4cc8c1b2dd8a318a06686a0500000000fdffffff4a2c8e9e650ec859ca8ca1f63da4791284c8e09c8eed9d0e641962c8fe38f4e60300000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001976a91479f7962428741fa25e70036f6719e2c11efa75d388ac409c0000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac0247304402205f1880ff29ba4221e8f2c84cde5264265a56c15b675b7d1d9f809e9517b3707202205b7e4d87b6cf9628981289c0c8f0d89b3a84d935b5a990f86f6020106288e1b10121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a64024830450221009639f233b83e22acc73cf2404a3e7e836ee1bffc86b851ded700977d3fe0510c02200405411259c15a8a1c3fc60aeca76141bc0e0fca65310bf47fd21797a83872d0012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd8602483045022100f332608132dfaa944b70db4bdb1555bbfbf21cec5a58b3011b0a43f04860cd9a02204c6d8486783ee613c5e9ed24fa973e37fbc951e29f3718bc7c46ba9f1c6303e5012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402473044022043446f3427d8ab7d0ede536d79c84e1916722a54e6d2d86df59cf066953fb3fa02200f5aeb684f16f3716c1c8b4c727f8132b3c7dcd802b30abfe2a56cbb0d3c33c9012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100b819dd2102300351373b17624dc6c83e00b1c094233eb517feb21b417c3f015102205273f4b1e28611d79ab5487d6f6bfcb2a3d294ef77be2803d498d995c569d5b40121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc4e61bc00");
    }

    private void verifySpendTx(SpendTx spendTx, SpendType spendType, Collection<UTXO> utxos, long minerFeeTotal, long minerFeePaid, long samouraiFee, long amount, boolean entireBalanceExpected, long change, BipFormat changeFormat, Map<String,Long> outputsExpected, String txid, String raw) throws Exception {
        verifySpendTx(spendTx, spendType, utxos, minerFeeTotal, minerFeePaid, samouraiFee, amount, entireBalanceExpected, change);

        // consistency check
        Assertions.assertEquals(UTXO.sumValue(utxos), amount + samouraiFee + minerFeePaid + change);

        // verify tx
        verifyTx(((SpendTxSimple)spendTx).getTx(), txid, raw, outputsExpected);
    }
}
