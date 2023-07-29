package com.samourai.wallet.send.spend;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.Chain;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendError;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendTxSimple;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

public class SpendBuilderTest extends AbstractTest {
    private SpendBuilder spendBuilder;
    private BipWallet depositWallet44;
    private BipWallet depositWallet49;
    private BipWallet depositWallet84;

    private static String[] ADDRESS_CHANGE_44;
    private static String[] ADDRESS_CHANGE_49;
    private static String[] ADDRESS_CHANGE_84;
    private static final long BLOCK_HEIGHT = 12345678;

    public SpendBuilderTest() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        spendBuilder = new SpendBuilder(utxoProvider);
        depositWallet44 = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP44);
        depositWallet49 = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP49);
        depositWallet84 = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);

        ADDRESS_CHANGE_44 = new String[4];
        for (int i = 0; i < 4; i++) {
            ADDRESS_CHANGE_44[i] = depositWallet44.getAddressAt(Chain.CHANGE.getIndex(), i).getAddressString();
        }

        ADDRESS_CHANGE_49 = new String[4];
        for (int i = 0; i < 4; i++) {
            ADDRESS_CHANGE_49[i] = depositWallet49.getAddressAt(Chain.CHANGE.getIndex(), i).getAddressString();
        }

        ADDRESS_CHANGE_84 = new String[4];
        for (int i = 0; i < 4; i++) {
            ADDRESS_CHANGE_84[i] = depositWallet84.getAddressAt(Chain.CHANGE.getIndex(), i).getAddressString();
        }
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
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 148, 148, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "2dd7a7cd2b7bba56e5e1f42ca10a9a6ba8ebfc82024dad38b0bf18b33b0dfcbb",
                "01000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff027c26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016410270000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550248304502210096ced5ea7f944e307f399884eaa72709fcab71685a89377fb36e0ac339aa644702204fa5a55b57e115f30c97266cfef6af67b77a74bbf2668eaa55f7b38e88fa56b50121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
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
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 148, 148, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "f209926ee994ea6aabadc7b462b58f0f1e630d3b0e2531540b200c746e41086b",
                "01000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff027c2600000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87102700000000000017a914336caa13e08b96080a32b5d818d59b4ab3b36742870247304402207d0743e0da058e984842616a8e48e8615469b9f20da113ceb90207388e058034022018dc6369a76ff2502e6efe9db23f4f94505c2ee82c4d546d423abecbffede94f0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
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
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 220, 220, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "146190f469431b90ea250a576a25a34dea7091170e05eb415894b41b82ced44a",
                "01000000000102e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff023426000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502473044022019a18e1ed5b9199a583a732114a56bf0fbb23f7def76a6c2d1a5d16445cd4a9702207f32c98298718f4b42bd0b29d1c5ceb3e6bfce518a0e3a9c347e9c62b024c28b0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100e17d9af2060e258c2202e775e40d227b0b1c62ac12783b2dabd68b93b173529e02203b3989d53e5e237990a2ee62e647a9fae46614380ecd0c142ccceeff24e148980121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
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
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 292, 292, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "70c63659c3a994a1c82c8d0110d7369cfa17ab54b3e04e2c5d3bbdd2bedb3106",
                "01000000000103e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff3e3f0c4d7c92472be300847fa18669620222af52c416d307c519c2a7c544fabe0100000000fdffffff02ec25000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016450c30000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d127355024730440220545f477e255dbccc388c6c6ced1ccffd5ccb6437a80fd5b04c18aae75970aa99022026384758f68dc460287c534af60a051d2bf19e5229444bdb090be5c5a48a46580121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100e1eb3929f32af3e091074975e512869fef1635e278533fa69efee9725b72c9da022062f6eea44734ca5275afb72ce813a986da85cfb28e34ba69dffb762176987d4a0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a64024830450221009521533cf3596e97a479d2d4de6846119375c2a0e2ddb5073c7529f819b4a4c90220389bd07df2a9b98c97e98ba0b210d55433073cb0530fdcc298928975018195a201210229950f82d3230b06db77c9aec65180e5bbf99fd837fe967dbabe4d9b0422146c00000000");
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

        // should select Boltzmann
        SpendTx spendTx = spend(ADDRESS_BIP84, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], amount);
        outputs.put(ADDRESS_CHANGE_84[2], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 502, 502, 0, amount, 69498, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "43546f7409c2e3cd55bebd6902cd5be29f416f00409a70ddbaa008a5d9955bad",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600144910e17f5ca698222657369753a164262605087a409c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550247304402207097350bffd0d270bd009919e755c3ca1943776a33acef964cfbcb3093a206f9022045ca6fc9f0c4c06cfcf30b8704a94c5eeaad65c0a7b0eebada41efef8382d3f90121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc0247304402206cdd95cc08a234431d4682c835df75a7e862be84a0888ccb258915f9972461180220328202770891fc84adf9c6739727134691ed555142b7549218eb875000a42118012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a40247304402201c1280f48a000c760c0fb6efb731356c307a9e9ca5f5612006d6370020e879a402201e67bdf0ee233307d44c948583111ed69813745a54ce053f1d5bc0850e785399012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100a4ec6ae79b5dffbae3303e507f7d9ecb6a4d0f027820d40818223ee745c1fd7602207592e937be7cdee05a575186060f911129cf2a2c6d3f7e59a56e7ca3fc3d4cff0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100d31c0af01445ed5047f89e6bdf8c8dedb3085a258a6868540fedc3d298af2d590220796e136280d94e849698840f3453383592c49ba180fc258a7f96bbe6aae5def8012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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

        // should select Boltzmann
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 502, 502, 0, amount, 69498, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "986d57dace49b9940725062f437fcce8ff5515984c12d01e16e9f9a1c14ce55d",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c00000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87409c00000000000017a914336caa13e08b96080a32b5d818d59b4ab3b36742870247304402206397878e330dd583f10ff2f91c6ae5695e5f4e005a60946100930a99def1100e02200feb7a9a5a60aea97956fd86d1d6861e8af5d042e81645f53a99cb16ee07bb750121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100bb12cad0973501a0feab6f434484e61e3efd6fa24ac33a022de8713bcac9ae140220604b1bfca8a58886b6ee712ef217e11ceb3e71b51a392dbd94783b825fc6c4e2012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a40247304402202aa6c66279ff88e9e00f6b0b43c77f3f86585992e0c0342f3ba49c6843a6a870022073de858c91cdc43bfb6255dbade799c9a6c98a3fc0ef15607148c73dee2286af012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302473044022059581d08c797f935109a066045077d408fb30ee7ab25c521b5a78c9e60ecd48c022021c6c227c9625b60a78e5915b40df707c0bfe00f5bf4fcffba6a74b168ef96d10121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100c6f806994c497ef833704f08587b38ac3642b4d4e1e697df01ba9186a7e0c4080220179cd0abaaa692bf5a24e6bbdeb08ecad681daec0e97854db7b65f780bd70332012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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

        // should select Boltzmann
        SpendTx spendTx = spend(ADDRESS_BIP44, amount, true);

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP44, amount);
        outputs.put(ADDRESS_CHANGE_44[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9749L);
        outputs.put(ADDRESS_CHANGE_84[1], 19749L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 502, 502, 0, amount, 69498, BIP_FORMAT.LEGACY,
                outputs,
                "9c0d091a4ce1e081a9995c39f4e0108c9ed9dea2924df915a7146a12bf6e0e9e",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041526000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164254d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001976a91479f7962428741fa25e70036f6719e2c11efa75d388ac409c0000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02473044022077424f33573eb5b1b7cdbff839528fd1c3739df7797211fcbef42180f606c7b1022056530f56a9e53ddc5310a01bca83331c08692d4c1241244f2ddb4f5c34841c110121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc024730440220268872226de403b814f21dfa6d3ff96ceed89864ce893cb6aa7bdaa8d33a5d6202205937aecb7d24d83a66c58cf9cc0dd80da6f235311e586abe1201e86a249d8eb9012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a4024730440220232a0ef43efb1e8cc01329591141784200ac70ce86e6e285e75c55ec1c6b4ef9022025bc749fe5fa2053c529e179194dfb4db89acc0e3094dbf820d27912d72d9e62012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100c50473f8ba369309c0e5f30cbc28963bfc095c29b0e8fd1bc0509bc4527aeb570220490fc7be14c5449a351a0993dd5d24b73d56e3e3a8bafb3f2ff654eaa7988f860121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a640247304402201d40bba6166b50c30220361490bc93e960500a28a166d14ed51936ba09a94f67022029c4e6c753de3d9ef030d91f250dbfd6c492577da79d8bfcee261aa3a94d28a6012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
    }

    private void verifySpendTx(SpendTx spendTx, SpendType spendType, Collection<UTXO> utxos, long minerFeeTotal, long minerFeePaid, long samouraiFee, long amount, long change, BipFormat changeFormat, Map<String,Long> outputsExpected, String txid, String raw) throws Exception {
        verifySpendTx(spendTx, spendType, utxos, minerFeeTotal, minerFeePaid, samouraiFee, amount, change, changeFormat);

        // consistency check
        Assertions.assertEquals(UTXO.sumValue(utxos), amount + samouraiFee + minerFeePaid + change);

        // verify tx
        verifyTx(((SpendTxSimple)spendTx).getTx(), txid, raw, outputsExpected);
    }
}
