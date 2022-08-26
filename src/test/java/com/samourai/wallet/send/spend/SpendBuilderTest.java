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
        long changeExpected = 9848;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 152, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "d8abf550b18902b2be05697f2c16bd09711d6fb716d29c79c8bee3861373f6a4",
                "02000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff027826000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016410270000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100c47769efd64087d644c3b1d541e17f86af93e685a2f293d0733b1062b1dba7be0220564489218df1f71bfe65bb6e8106c56e09188ecba8d67e52a7f648d5cae81cfb0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
    }

    @Test
    public void simpleSpend_single_bip49() throws Exception {
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend(ADDRESS_BIP49, amount, true);
        long changeExpected = 9848;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 152, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "4a2696701dd6ce22309413443f2c9f3e95864645794d2e32de9b8cdf1a31be34",
                "02000000000101799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff02782600000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87102700000000000017a914336caa13e08b96080a32b5d818d59b4ab3b367428702483045022100c615d99b6dbdffc394c5b223657221e001286a4f8671c08466881910cc01f330022028d291105c8dd27de81c92d83425ae1954287fb75b993748c4e8cc7086e76e5c0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
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
        long changeExpected = 9776;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 224, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "e4c5de2008f189427ba08470e65af526d72122f095d4010b40215a01ed7ff5ed",
                "02000000000102e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff023026000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d12735502483045022100b130223155708e879c53a8c9499c25ca4ebf9a36f93f5197951e3f43487f44480220499e47aa1ea2801f75c1e8419762220de906bd2184925d9bfa7a2df9e5ac04270121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc0247304402205b7e5d2ff74290693b36ad194b2beecfe7e9850cf552ca0b44beecc574fbc6160220208bf032ee812b069bcebc9fc5020f0762d9dbaef76849cd791c3553890211080121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a644e61bc00");
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
        long changeExpected = 9703;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], changeExpected);
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 297, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "7fbe60ff7c4777bdd13763121d3e012f49eb287e683f57fbc73d8c6cb09768c6",
                "02000000000103e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffff3e3f0c4d7c92472be300847fa18669620222af52c416d307c519c2a7c544fabe0100000000fdffffff02e725000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e016450c30000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550247304402201b0bb95180d7fa7f6c3930b87f29a96f209c3c9f5524bbde5b787f94b1e3323702204f8c4318c5d098da207510b139b9987cf3170363f1a95cafd14cd1b4373e074f0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100d8b2e79a432f7d8c329cbe5c77c74709c0bccc2f5afa1d6887c0f8a81ae308ab02204a2e7a8dcd25c8805b3b12729f4442d26252b6ee15d339e5635c591fb0aaa7a90121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100a76496760186215d3eeb9a5895743062d82bee0b9f13b1d8c78e5d7e3007f93502201924b1edd60d020ed6c47b0901790447531c6ad467812507c1e29018e5061aea01210229950f82d3230b06db77c9aec65180e5bbf99fd837fe967dbabe4d9b0422146c4e61bc00");
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
        long changeExpected = 69488;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP84, amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9744L);
        outputs.put(ADDRESS_CHANGE_84[1], amount);
        outputs.put(ADDRESS_CHANGE_84[2], 19744L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 512, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE,
                outputs,
                "89ca8c44e5d94940f2f93ecc90b1223d77e013f53d4dcd92e8fffe217ccfb2fe",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041026000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164204d0000000000001600144910e17f5ca698222657369753a164262605087a409c0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001600142ecf8c3e5697f0513999ed3aa8ea9cd04d1273550247304402205c075ebc50b4a3c739247410e95adad127061912a3b3f6b62b97ba56dce2d49e022012597040fcffefd4eab56789926f71d032259f6aa6826e03ca4850df922cae400121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc024830450221008b18c0b40830a3182becbdf5fded626e902de5fa204e4c56482be559a456ed12022014bb16a4aafd08fcccbaa54e1f6ec5550ca41fa31bd3cadc0efd69712d850aec012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402473044022077e2578f15f619b63eba90053ebcca34443ad1feeee4d902d0b5e146d088a7960220351de169c6ed05f0b52aebbfd8c3f75b4c1e52bbb6b5d285050ae96debbd419c012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302473044022049a793512df8f0b3e5e5e8f8aed4ba970ae58a8dcc07bf7fde0a59639ef4ce6c02202e2833b846b97d9e57fc8d9e3544f2be4fed8b725a3ee738cf98f819828cbf8d0121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100a3e7247e4be4a746b279d59f2ba5f189992f4685a3ad7b01bd078e09b827d5830220745c98381ea9ce1acf1f7d91c4925f68eec9d6b06292e74a5888605013464cc3012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        long changeExpected = 69488;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP49, amount);
        outputs.put(ADDRESS_CHANGE_49[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9744L);
        outputs.put(ADDRESS_CHANGE_84[1], 19744L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 512, amount, changeExpected, BIP_FORMAT.SEGWIT_COMPAT,
                outputs,
                "1f4e0003ab421605aaed5438f7d1f7a5892f0fab105fe1598fd5e896228044c7",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041026000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164204d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c00000000000017a9142df6e5e10a713bed1d1753d78285c386dc20f34a87409c00000000000017a914336caa13e08b96080a32b5d818d59b4ab3b36742870247304402204845758fd064cc8d75d5ddb78ad80d45cbfbe4dc00bcca4fb3b2bb02bf22e195022049cc9842d448a76e1e0acf38ccaaf7d200968e98ba818314ea829f67d9840e6f0121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02483045022100ca5cd3bb04dcc3d78e708bad73bca9018b0ed077689e8e605c4cfcc5ef3f662e02202f292ab2d752862ada603dce1b345de3ffbad17c963e6e4ee7312bd6eb2f507a012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a40247304402201e7ce5001cc5f068ea2f141da3413ede5520a8571d5dc6aedebc2c37a2ee62ff02205d47517239b8d9fe0b3d6d153fd25eba5047ed6de06514622bd0682e768105df012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec302473044022033ce5be58f7dfd9bb6a902c363e76cad87f17776af8017f12fbf0dc860f61d0102202a8c762869d3ed73194efcc3e90e107f06b6edd5ceacdd7e03d2dadd1b29d8620121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a6402483045022100d243f7aecb3a85974edfd51c3b3a3f20c058ecd9c744b61d8a8c45c010e7c2c202200fc0175ea96c51161dcda90ddfe8896d5121d04c7e86949f68b2dfb7e2968be0012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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
        long changeExpected = 69488;

        Map<String,Long> outputs = new LinkedHashMap<>();
        outputs.put(ADDRESS_BIP44, amount);
        outputs.put(ADDRESS_CHANGE_44[0], amount);
        outputs.put(ADDRESS_CHANGE_84[0], 9744L);
        outputs.put(ADDRESS_CHANGE_84[1], 19744L);
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 512, amount, changeExpected, BIP_FORMAT.LEGACY,
                outputs,
                "e2d36c9ceb12409542526df420b3e6740f6811c0769e50d6ebadba3a86934ae8",
                "02000000000105e93c5d03e89545aa69c933aa7818f2ef6fe0a2fb3d2c6193085c6306538be50d0300000000fdffffff82df04ea95d5725ae17c68ea40b3af7ea715d138e78ebd985be21a951e26c42d0600000000fdffffffd0e61bb0ec377930b4aafd6c9fb29ced8c4035a1a91947956a32e9801546273c0500000000fdffffff799901cbde3146618639ddadf2ba79e568e2ed263f9aa046befdef30c681918a0200000000fdffffffb6f61fac50b91214fd857f604b57fc111296d2316f20e625a247b0d368385df20400000000fdffffff041026000000000000160014f6a884f18f4d7e78a4167c3e56773c3ae58e0164204d0000000000001600141bd05eb7c9cb516fddd8187cecb2e0cb4e21ac87409c0000000000001976a91479f7962428741fa25e70036f6719e2c11efa75d388ac409c0000000000001976a9149bcdad097fa4695a3bdab991e3212da04002ce4088ac02473044022016119d8c9f4d28769f0cf41e1d3a4221197c2d963e9918aeb7488c04ef3b95db022029f5fcc15ba90c93c8ddfa43fe6e8eaa898b1a2a90cb175d2578be24733a43d70121032b6f9c80bcba58d5da83f8270a1a19a8f19109223d9e3df3c298bd6468bba6cc02473044022061b8faa2ddce5926ed4d6671956c0b262e223193475674eb4ca118fba257971b02202406829990c3a3eeeb8ffd8a44dd299371adeb76c11da9b57db88c682174f916012102b2a7e1a1911083a9a8750921de866e4f14afcbe0d3d9d934fecb5b6a5c0ce3a402483045022100c913f3569d6cd757c072e0e4739b44c0c309129a787c80bc5a7fb760291261c802202898b98f2937ba70f21bd30cd045aeb25ca142f4df1cea1d77dc4d668c075ad3012102bd1d0567294f540aff84cbbe0d999b81f58a2357939ca734576de20ec3730ec30247304402206eff21ada9938458259b4943c46e9f4e0d53db9e44447d437acb4dbec8371840022028fd729ea6f2ea89fbad5a110f02e292cccd134c142546a7b42cd97b245846680121020fd264b3db00aa2c43e4688e824f5a6e5247b8474620c3ce53cd20316a457a64024830450221008d874a076185e3a614816173708bb3bca48d8aa0db19ff7b7a478c9e4dfec39502203416108deb164f32ccda20b10afb3a07b1a8e4a57dd220c6fd92e0cf3d3c429a012102fbb6632bb7263a07d4f3e43ea6e92922b86d813785068145c49de168ed21dd864e61bc00");
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