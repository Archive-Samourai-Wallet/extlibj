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
        long changeExpected = 9846;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 154, 154, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "50a48a8d119b94d3c600617fb8e4659e09d934dcb5abd3f75138188ef9e02e90",
                "01000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff027626000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016410270000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100a1c0239762f1a96bf26f544068326dad9ae189c3aa03ae18c4d76821b36810f502200b78a17a47334e09a61357159d0bfcd15ee4d7183607cfc2a44288c61b1bd19c0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
    }

    @Test
    public void simpleSpend_single_bip49() throws Exception {
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);
        long changeExpected = 9846;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 154, 154, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "482641a2d478ec3b2889fa119bd9c31fe914feb7f8024e8c89f26b457caebab5",
                "01000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff02762600000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87102700000000000017a914336caa13e08b96080a32b5d818d59b4ab3b367428702483045022100aa08a43b5c22519d1c665f967de942cc37df443ed7b1c7a0c51e687f7df2a8cd0220055b85f6f563c36fcc2d66386a8cbfb24aeedc6345de336e585cde29717ee0fc0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
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
        long changeExpected = 9774;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 226, 226, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "52f03d341393f93e524db8feaae3b23846938e750d9f63b543c1005e3a923c46",
                "01000000000102e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff022e26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100edb4322f0df11cba22f866b21527b9abe1001d33aaada607d05583556bd95be7022004db9e064bec0ebfdcefec82085148893b5235222b88a2ffd423fb97c5e6c1b20121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100fad13666b0489575b6e2560a5df0bc47bba9b52a2a02b80e40817198d0f00d1d022020500879b9edb3ac2b169a7e99169988e9bfe7f931e8c07c2954ff57a42868f60121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6400000000");
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
        long changeExpected = 9701;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verifySpendTx(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 299, 299, 0, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "6349f7eb62af4626673315f9a3b873cc42f74d979fa41ad54441a947823b23be",
                "01000000000103e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff3e3f0c4d7c92472be300847fa18669620222af52c416d307c519c2a7c544fabe0100000000fdffffff02e525000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016450c30000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100d102f89dd69a2b1016b5d7c9923e7f83b349a4aabd6cd0a82877d887d6f5141702203375aa7221a697e5bc146cf6d0f616de7e5e92374564a5973c01f0307db5f8af0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02473044022026a99dbc9a53a2de146dac29d38dec50c98d31e19b5560a61b6373982e81b73102203ab72d032f7a4264b8ea7fba3a2a95e270a5b72ea4b5d8e66f5de5e00b1ef3ff0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402473044022035b210e0fa68710a425a25ae429fd538bb11a30a8f6dcda735db56a64e29381302200c4b190823a890e8737fed8693f6025db83f93ded3ff4a9d1a7b2a883fb6d54501210229950f82d3230b06db77c9aec65180e5bbf99fd837fe967dbabe4d9b0422146c00000000");
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
        outputs.put(ADDRESS_CHANGE_84[0], 9742L);
        outputs.put(ADDRESS_CHANGE_84[1], amount);
        outputs.put(ADDRESS_CHANGE_84[2], 19742L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 516, 516, 0, amount, 69484, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "78bc96f67ae581e36a1b8a5a178a84a046a16c1f4cb57800319e1b1b72fc40bc",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff040e26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e01641e4d0000000000001600144910e17f5ca698222657369753a164262605087a409c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100892193ade3463ca98635eb170dab51861a6fedecdd162fce8af9dda9d974bca702203da3132425fdb6266b95797cc10b792e4670cc747be89a1d73b0c02356d90b970121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02473044022014cd5650c112b3d4edb705325cb1ebc464bf3b14485b5d6717efa3a8fae653fe02200a5bf0b65b52f4158a30464d3e9f3514c144609eb9a1726688ca448df3901308012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100ba94a4393b8edd46e3e56ef931439166d129badef6047f21325cb6a86b60dc7a02203757f407fe7457a5115d127379fe0226d811c2fef7573f816ea0ee6a9acd084d012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec30248304502210091e26aaf2b933a4fd442669f07d8c1b2e538ac97c7ad0a268c9ad1e9553d24a702200f4f116631cd5b549d74a24b9bcbd957b91a25875e11a513a1063dd4f2e6fc2b0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a640248304502210083f6481e472b8130f10ff361d562cc014be70c4eef7d94219dea5e006b6db3d6022056d12482497a44973faa7a24c867c1a29bf15808eb59822b4aaf480d564878a8012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        outputs.put(ADDRESS_CHANGE_84[0], 9742L);
        outputs.put(ADDRESS_CHANGE_84[1], 19742L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 516, 516, 0, amount, 69484, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "e754ba2d9e6e9114d3cbf82fea0d683431335ca559e5e05c3a2c90f599caf19c",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff040e26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e01641e4d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c00000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87409c00000000000017a914336caa13e08b96080a32b5d818d59b4ab3b367428702473044022076cf87ef81cafbd015a7a1542b2eb85cc7f1c19af59cd49f33a4e85d02a740f50220614134de3c4375d4539bde11a0591fa7bda4219397af0743a2edc049e6131b120121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100c710df4cb624ef490faf0aed6b5c3fdb37dcfd0168e7948c5baee654b78ac61b0220528ca29f7b66e6b6cdae2a23a6123edf7a6683671324bb2bbc31d1b1b0651da0012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402473044022067dea6ff8f20c62e4290ad76d8a9d3e7094806d7c8cbee005797413810606afc02200b45ee4bdf77f42a10d1ea0dcf77a370afab76ab09a2b0c770fe0ed2b9692b9b012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec30247304402200c258519b6677d64836d7e572816ee676bf23ee55d0ae8fe4c21be0f945be3e202203b504452c0f7bd574b4ac9baa13bf2bc689606beaa859a8fe1af8460d7e432ed0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100edf7ae064ec64625927fe7152534500b7541904fa7c9ed134c751a5887312e11022012ae877794b984dc747765fd8d386f605e10c8b101065edbdbe7e76d15427ba0012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        outputs.put(ADDRESS_CHANGE_84[0], 9742L);
        outputs.put(ADDRESS_CHANGE_84[1], 19742L);
        verifySpendTx(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 516, 516, 0, amount, 69484, BIP_FORMAT.LEGACY,
                outputs,
                "49023023faf546a045f63a1b8e675a40a9b3a6ad22bb26f9835d3430310f4fe4",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff040e26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e01641e4d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001976a91479f7962428741fa25e70036f6719e2c11efa75d388ac409c0000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100feb694632373d9334d8b67f82a28d989cc93391ee53b9f1936898d7e9583438302201b1d40b66aca9fc9f2e0cac2247193115dbe26c4606e0eed1b5b318f5309effb0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100f3dddf30dc0b4dd154bd8096d54a4e579fd6efeeba216a792b56402fcaa622de022065ad68dd975808be0af058c4eb4f9507b40b5015dc3738308ff7be834ab6ff4e012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100933b1c345aff0898b6352661254bf0fb1cc928e0a63fa9b94402605c224bc945022019389c5365bc9711d62235b5921a9e0622d30bbe7806591007d70fd144212fc8012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100bd5678a70607a34b2615c29bd1fb907605de824ebfee5aa3badca9f0ea0e145d022016945ff1efb180c5540e8cdd3abd79bad1edfbf6e33aa90b212364d5cc93f3fe0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100d05edcaa424b639f3e2eb4fe08eaa92e7c29bed41c5319bb4f329cb37d9568ba02207ea9aaea0905e10c8821bc624ab1d2db72f8c35a4dbe65fd3c98b370dedb5d9d012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
    }

    private void verifySpendTx(SpendTx spendTx, SpendType spendType, Collection<UTXO> utxos, long minerFeeTotal, long minerFeePaid, long samouraiFee, long amount, long change, BipFormat changeFormat, Map<String,Long> outputsExpected, String txid, String raw) throws Exception {
        verifySpendTx(spendTx, spendType, utxos, minerFeeTotal, minerFeePaid, samouraiFee, amount, change, changeFormat);

        // consistency check
        Assertions.assertEquals(UTXO.sumValue(utxos), amount + samouraiFee + minerFeePaid + change);

        // verify tx
        verifyTx(((SpendTxSimple)spendTx).getTx(), txid, raw, outputsExpected);
    }
}
