package com.samourai.wallet.utxo;

import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtxoDetailComparatorTest extends AbstractTest {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void compare() throws Exception {
        UtxoDetail utxo1 = new UtxoDetailImpl("utxo1", 1, 10000, "myrDKvdCUNAMEoxWT4r3116i21R93s5vUV", null, params);
        UtxoDetail utxo2 = new UtxoDetailImpl("utxo2", 2, 20000, "mpZYLcbacAdehXp3max1h2Lubk4GRnnpLj", null, params);
        UtxoDetail utxo3 = new UtxoDetailImpl("utxo3", 3, 30000, "mhAaH3UGm6NYeHHvHx3KRGGCddwYdBj3VH", null, params);

        List<UtxoDetail> utxos = Arrays.asList(utxo2, utxo1, utxo3);

        Collections.sort(utxos, new UtxoDetailComparator());

        Assertions.assertEquals("utxo1", utxos.get(0).getTxHash());
        Assertions.assertEquals("utxo2", utxos.get(1).getTxHash());
        Assertions.assertEquals("utxo3", utxos.get(2).getTxHash());
    }

}
