package com.samourai.wallet.send;

import com.samourai.wallet.test.AbstractTest;
import com.samourai.wallet.utxo.UtxoDetail;
import com.samourai.wallet.utxo.UtxoDetailComparator;
import com.samourai.wallet.utxo.UtxoDetailImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UTXOComparatorTest extends AbstractTest {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void compare() throws Exception {
        UTXO utxo1 = newUtxo(1000);
        UTXO utxo2 = newUtxo(2000);
        UTXO utxo3 = newUtxo(3000);

        List<UTXO> utxos = Arrays.asList(utxo2, utxo1, utxo3);

        Collections.sort(utxos, new UTXO.UTXOComparator());

        Assertions.assertEquals(3000, utxos.get(0).getValue());
        Assertions.assertEquals(2000, utxos.get(1).getValue());
        Assertions.assertEquals(1000, utxos.get(2).getValue());
    }

    protected UTXO newUtxo(long value) {
        return new UTXO(){
            @Override
            public long getValue() {
            return value;
        }
        };
    }

}
