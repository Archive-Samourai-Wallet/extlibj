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

        SpendSelectionBoltzmann._setTestMode(true);
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
        long changeExpected = 9830;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 170, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "eb658b60f1a7f02d335b9b579bcb7c40ea4e1e28fb21e346c585f6f43d98aab5",
                "02000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff026626000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016410270000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100c050d9125c6eedec8317145cefd51e7a3658b77a17d61c5f41a502026a76875102200d550751685951d20108b16aca4aff83fe6e2b13d5886c38f27f4d404addce060121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
    }

    @Test
    public void simpleSpend_single_bip49() throws Exception {
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);
        long changeExpected = 9830;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 170, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "95db6e667f365aaaf14f3c90bf31c40c7a1d35e52d8b63cc1328d7f10d66762f",
                "02000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff02662600000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87102700000000000017a914336caa13e08b96080a32b5d818d59b4ab3b367428702483045022100b9f0909f18a50fa4ab5da5ebfbf676a92805dacdea67cab4c536c05b8c961074022032cad5b3f361d4df5e433f6cf10fa45f2e2c2f34a314b1cc4fa6f7153741e2350121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
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
        long changeExpected = 9740;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 260, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "4c08c69c6a7906af1f808bcecfb6a71c366c2d697156bd9fa7795ac99292ad73",
                "02000000000102e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff020c26000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d127355024830450221009898d27f5a310eb15f361da51049074cae1d3276ec7a6d936afcec09d90ea4d10220585d9ed994c6d6796515c608b821a7ba6539ff0bcb3277ca403d452c3a0feed90121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100ca0b8d08e74b750503386d378434c7142a4d7da51795af050f192d4e93dce80b02207dc9b86edd36a10dfa34eee014d0dc3451696819f795a97864c5d0008a815c030121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
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
        long changeExpected = 9650;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 350, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "eb34c7a6c967216640e267b872ef41ecfb59248994634812f84c8129d68be0ae",
                "02000000000103e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff3e3f0c4d7c92472be300847fa18669620222af52c416d307c519c2a7c544fabe0100000000fdffffff02b225000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016450c30000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100cef96bf379a3b86b7eceb6dba683e22c30d3e98ca5f3b912233b0290bc9c26e3022006c6141e93b1a75e60eeaec1b5ee5a2e4805cd84187f02edf51bcc718efba94b0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100a6eff9ef88adf9d35758ea9abed19e79c0dcd45279615f614d2dcedec71aa0e202201214709cddf30b01164ae97af9760d973625b32426ef92be0cd041169bc5bb010121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100e907b8e7096276bd1426ef697905f0a9d04113204193a261c5ff1e05405f2ec7022075bf9c22ec5f2e833806077e788e6abdb5573a69359dd693d15906ca24ffd13801210229950f82d3230b06db77c9aec65180e5bbf99fd837fe967dbabe4d9b0422146c4e61bc00");
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
        long changeExpected = 69400;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9700L);
        outputs.put(ADDRESS_CHANGE_84[1], amount);
        outputs.put(ADDRESS_CHANGE_84[2], 19700L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 600, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "ecc174d9a8dd609c7b2a0bbfd345576a21c73933854ff0f375ad420ac43b5ab4",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff04e425000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164f44c0000000000001600144910e17f5ca698222657369753a164262605087a409c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550247304402202eba27512d56eef16cd6afd688f59871cafb15f22d458bdd41918866a2049b1b02202bbdf94044d2bc1d647630ecc2ce590d2656729b1d71f021700be87d53d25a4e0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100b98b6427890da9c9d4462cb9e0f01164cd0dad8c55a6977c7d36b8164feebe9602205ef4611d6f7712bee7cae3a9652672cb8fdde9e2c5fc9ad682d50e298bdc39c7012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402473044022027165e48d1ea92e60ee042d01db99383f95edc45441dbfaffa4f5e80db0a9e12022022d8400e93b48ecd2aa2ddd1f4d16a0632706131b4f2e081e41293d18d8f779e012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302473044022100e5c0868f29ac080075924c40d3e0968aa1e543ca4a13b47823d071fceeb6f1d3021f0de122a38c60ae72bbbef5bec05880f1f9160a75af35f01f070138c580d8f20121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a640247304402205a87c65cb41587b932ed71a6c13b1060d39e463246d86f8edbef6af6238ca0bc022078017535749358df12061c9923896b936ad70f2ddc85bb7363e455ebe7c5dc26012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        long changeExpected = 69400;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9700L);
        outputs.put(ADDRESS_CHANGE_84[1], 19700L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 600, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "862b0037009f6a6d8e92c927a77879e0214b981a3fd0905e115cef9ca3070d6b",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff04e425000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164f44c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c00000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87409c00000000000017a914336caa13e08b96080a32b5d818d59b4ab3b3674287024830450221009baac402eb2d3242edcb212dfff1fda4d6b7f619be2aa4c2e48316d7e710109902200630e0e4b738127e8d001a7a0bff05bf0ca1d1955c05812ed2fd02a279b7459a0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc024830450221009fe223d6346622f6462a2af56c59ecde0e01883c3d650ba04af29294c9405d6c02207860f0003db4b236a3bc382433f547e6354d92f28490f16d4e8595d8c81240e5012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100a0949303ddf8781fe0fd09ee6e7517880db8f7e8481746b5f18c3602cda080cc022063c532003868242ca873174a999b614335ec6e0065ecc556b390ba53d5f75798012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302483045022100e79b8108a4e0f47c6790238cdee94624ca25a062de3eb81a54ec0ec903ad1146022003851afd6aedb422023a0ec60001879a57c7b7fb619dea1def1640df39582a910121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a64024730440220330b7572e41a9e1453bb5717a3525f0f181832eb1e8766ff3baa07f8bd9d83f802204835e2140f52a91b1510d0646e41e54c8b9cb646493007c26c1d1b50260a9846012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        long changeExpected = 69400;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP44, amount);
        outputs.put(ADDRESS_CHANGE_44[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9700L);
        outputs.put(ADDRESS_CHANGE_84[1], 19700L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 600, amount, changeExpected, BIP_FORMAT.LEGACY,
                outputs,
                "5fca8410cf484ed6f8811e64b99758ba7d9fa2b06adf01ad5c49b44da8d5f8d0",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff04e425000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164f44c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001976a91479f7962428741fa25e70036f6719e2c11efa75d388ac409c0000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02483045022100f823a7165a7d6c0d80eb071f3bea68773f10ca31656098db2cac8c4fdbe29e2502202d6766a52140fce78b353cc7747a6bf6f9663b38b561b120330b72764f69516c0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc024730440220402ce2f7cf3635572c8811565e708bb9bbad3bb9a1f74ed7b39b5965914e2def02204516568cf8d53146aa57791f390e57147e8b63458accfacb98c83c7bdb1b4745012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a40247304402204b56e382c570561fa15f2143fc2e0688593b2ef617a648edeab915daed76d0f80220518c8b86f820ecef264491a705a504daec0a2ce67377b0c084823e83b53813b7012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec30248304502210081e694c01dee96421d741afb37c48c4e97567b41646cb62eec4f0abe036bf78202203a02218fd43416f4e69ca0835df7e7b0ea51b8ba791937e8954eb0d223417dba0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100f678670edaab13edbd5e9bff074a8a951fba014e914b31fa524eb946487359dd0220366127c623eaf9e7064c88e77b6c4ecacb08003b5d20ef28033d4640ae838572012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
    }

    private void verify(SpendTx spendTx, SpendType spendType, Collection<UTXO> utxos, long fee, long amount, long change, BipFormat changeFormat, Map<String,Long> outputsExpected, String txid, String raw) throws Exception {
        Assertions.assertEquals(spendType, spendTx.getSpendType());
        assertEquals(utxos, spendTx.getSpendFrom());
        Assertions.assertEquals(fee, spendTx.getFee());
        Assertions.assertEquals(amount, spendTx.getAmount());
        Assertions.assertEquals(change, spendTx.getChange());
        Assertions.assertEquals(changeFormat, spendTx.getChangeFormat());

        // sort by value ASC to comply with UTXOComparator
        outputsExpected = sortMapOutputs(outputsExpected);
        Assertions.assertEquals(outputsExpected, sortMapOutputs(spendTx.getSpendTo()));

        // consistency check
        Assertions.assertEquals(UTXO.sumValue(utxos), amount + fee + change);

        // verify tx
        verifyTx(spendTx.getTx(), txid, raw, outputsExpected);
    }

    private void assertEquals(Collection<UTXO> utxos1, Collection<MyTransactionOutPoint> utxos2) {
        Assertions.assertEquals(utxos1.size(), utxos2.size());
        Iterator<UTXO> i1 = utxos1.iterator();
        Iterator<MyTransactionOutPoint> i2 = utxos2.iterator();
        while (i1.hasNext()) {
            UTXO u1 = i1.next();
            MyTransactionOutPoint u2 = i2.next();
            Assertions.assertEquals(1, u1.getOutpoints().size());
            Assertions.assertTrue(u1.getOutpoints().get(0) == u2);
        }
    }
}