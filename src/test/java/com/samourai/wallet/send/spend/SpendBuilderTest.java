package com.samourai.wallet.send.spend;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.client.indexHandler.MemoryIndexHandlerSupplier;
import com.samourai.wallet.bipWallet.WalletSupplierImpl;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendError;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.send.provider.SimpleUtxoProvider;
import com.samourai.wallet.util.TestUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.Arrays;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SpendBuilderTest {
    private static final NetworkParameters params = TestNet3Params.get();
    private final SpendBuilder spendBuilder;
    private final SimpleUtxoProvider utxoProvider;
    private BipWallet depositWallet84;

    private static final String ADDRESS_RECEIVER_84 = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
    private static String[] ADDRESS_CHANGE_84;
    private static final String SEED_WORDS = "all all all all all all all all all all all all";
    private static final String SEED_PASSPHRASE = "test";

    public SpendBuilderTest() throws Exception {
        final HD_Wallet bip44w = TestUtil.computeBip44wallet(SEED_WORDS, SEED_PASSPHRASE);
        WalletSupplierImpl walletSupplier = new WalletSupplierImpl(new MemoryIndexHandlerSupplier(), bip44w);
        utxoProvider = new SimpleUtxoProvider(bip44w.getParams(), walletSupplier);
        depositWallet84 = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);
        Assertions.assertNotNull(depositWallet84);
        spendBuilder = new SpendBuilder(params, utxoProvider, () -> {});
        
        ADDRESS_CHANGE_84 = new String[4];
        for (int i=0; i<4; i++) {
            ADDRESS_CHANGE_84[i] = utxoProvider.getChangeAddress(WhirlpoolAccount.DEPOSIT, BIP_FORMAT.SEGWIT_NATIVE);
        }

        SpendSelectionBoltzmann._setTestMode(true);
    }

    @BeforeEach
    public void setUp() {
        utxoProvider.clear();
    }

    private SpendTx spend84(WhirlpoolAccount account, long amount, boolean stonewall) throws Exception {
        // spend
        String addressReceiver = ADDRESS_RECEIVER_84;
        BigInteger feePerKb = BigInteger.valueOf(50);
        BipFormat forcedChangeFormat = null;
        List<MyTransactionOutPoint> preselectedInputs = null;
        return spendBuilder.preview(account, addressReceiver, amount, stonewall, true, feePerKb, forcedChangeFormat, preselectedInputs);
    }

    @Test
    public void simpleSpend84_noBalance() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
        long amount = 10000;

        // no utxo

        // spend
        try {
            spend84(account, amount, true);
        } catch (SpendException e) {
            Assertions.assertEquals(SpendError.INSUFFICIENT_FUNDS, e.getSpendError());
        }
    }

    @Test
    public void simpleSpend84_insufficient() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
        long amount = 10000;

        // set utxos
        utxoProvider.addUtxo(depositWallet84, 9000);

        // spend
        try {
            spend84(account, amount, true);
        } catch (SpendException e) {
            Assertions.assertEquals(SpendError.INSUFFICIENT_FUNDS, e.getSpendError());
        }
    }

    @Test
    public void simpleSpend84_single() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
        long amount = 10000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);

        // should select smallest single utxo
        SpendTx spendTx = spend84(account, amount, true);
        long changeExpected = 9830;
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo2), 170, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE, 141, 561,
                Arrays.asList(ADDRESS_RECEIVER_84, ADDRESS_CHANGE_84[1]),
                Arrays.asList(amount,changeExpected));
    }

    @Test
    public void simpleSpend84_2utxos() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
        long amount = 40000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 500);

        // should select largest Utxos
        SpendTx spendTx = spend84(account, amount, true);
        long changeExpected = 9740;
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2), 260, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE, 209, 833,
                Arrays.asList(ADDRESS_RECEIVER_84, ADDRESS_CHANGE_84[1]),
                Arrays.asList(amount,changeExpected));
    }

    @Test
    public void simpleSpend84_3utxos() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
        long amount = 50000;

        // set utxos
        UTXO utxo1 = utxoProvider.addUtxo(depositWallet84, 10000);
        UTXO utxo2 = utxoProvider.addUtxo(depositWallet84, 20000);
        UTXO utxo3 = utxoProvider.addUtxo(depositWallet84, 30000);
        UTXO utxo4 = utxoProvider.addUtxo(depositWallet84, 500);

        // should select largest Utxos
        SpendTx spendTx = spend84(account, amount, true);
        long changeExpected = 9650;
        verify(spendTx, SpendType.SIMPLE, Arrays.asList(utxo3, utxo2, utxo1), 350, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE, 277, 1106,
                Arrays.asList(ADDRESS_RECEIVER_84, ADDRESS_CHANGE_84[1]),
                Arrays.asList(amount,changeExpected));
    }

    @Test
    public void stonewall() throws Exception {
        WhirlpoolAccount account = WhirlpoolAccount.DEPOSIT;
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
        SpendTx spendTx = spend84(account, amount, true);
        long changeExpected = 69400;
        verify(spendTx, SpendType.STONEWALL, Arrays.asList(utxo3, utxo4, utxo2, utxo5, utxo6), 600, amount, changeExpected, BIP_FORMAT.SEGWIT_NATIVE, 475, 1897,
                Arrays.asList(ADDRESS_CHANGE_84[2], ADDRESS_CHANGE_84[1], ADDRESS_RECEIVER_84, ADDRESS_CHANGE_84[0]),
                Arrays.asList(19700L, amount, amount, 9700L));
    }

    private void verify(SpendTx spendTx, SpendType spendType, Collection<UTXO> utxos, long fee, long amount, long change, BipFormat changeFormat, int vSize, int weight, Collection<String> spendToAdresses, Collection<Long> spendToAmounts) {
        Assertions.assertEquals(spendType, spendTx.getSpendType());
        assertEquals(utxos, spendTx.getSpendFrom());
        Assertions.assertEquals(fee, spendTx.getFee());
        Assertions.assertEquals(amount, spendTx.getAmount());
        Assertions.assertEquals(change, spendTx.getChange());
        Assertions.assertEquals(changeFormat, spendTx.getChangeFormat());
        Assertions.assertEquals(vSize, spendTx.getvSize());
        Assertions.assertEquals(weight, spendTx.getWeight());
        Assertions.assertArrayEquals(spendToAdresses.toArray(), spendTx.getSpendTo().keySet().toArray());
        Assertions.assertArrayEquals(spendToAmounts.toArray(), spendTx.getSpendTo().values().toArray());

        // consistency check
        Assertions.assertEquals(UTXO.sumValue(utxos), amount+fee+change);
    }

    private void assertEquals(Collection<UTXO> utxos1, Collection<MyTransactionOutPoint> utxos2) {
        Assertions.assertEquals(utxos1.size(), utxos2.size());
        Iterator<UTXO> i1 = utxos1.iterator();
        Iterator<MyTransactionOutPoint> i2 = utxos2.iterator();
        while(i1.hasNext()) {
            UTXO u1 = i1.next();
            MyTransactionOutPoint u2 = i2.next();
            Assertions.assertEquals(1, u1.getOutpoints().size());
            Assertions.assertTrue(u1.getOutpoints().get(0) == u2);
        }
    }
}
