package com.samourai.wallet.send.provider;

import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockUtxoProviderTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(MockUtxoProviderTest.class);

    @Test
    public void addUtxo() throws Exception {
        BipWallet bipWallet = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);

        UTXO utxo1 = utxoProvider.addUtxo(bipWallet, 100000);
        assertUtxo(utxo1, 100000, "befa44c5a7c219c507d316c452af2202626986a17f8400e32b47927c4d0c3f3e", 1, 999, "tb1q4crk5fzlr7qcz0nsun67luk982mn4wtlyydvlh", "02hNvy9WddFQ{17<N@0j-x7E?XQK");

        UTXO utxo2 = utxoProvider.addUtxo(bipWallet, 100000);
        assertUtxo(utxo2, 100000, "8a9181c630effdbe46a09a3f26ede268e579baf2addd3986614631decb019979", 2, 999, "tb1qfqd55aeuuhj6jl2v0v6ckudd7wecdv6ss9ands", "02d%yn{rzV</9V4DO{.3T[e(NVRQ");
    }

    private void assertUtxo(UTXO utxo, long value, String txid, int n, int confirmations, String address, String scriptBytesZ85) {
        Assertions.assertEquals(null, utxo.getPath());
        Assertions.assertEquals(value, utxo.getValue());

        Assertions.assertEquals(1, utxo.getOutpoints().size());
        MyTransactionOutPoint outPoint = utxo.getOutpoints().get(0);
        Assertions.assertEquals(txid, outPoint.getTxHash().toString());
        Assertions.assertEquals(n, outPoint.getTxOutputN());
        Assertions.assertEquals(address, outPoint.getAddress());
        Assertions.assertEquals(scriptBytesZ85, z85.encode(outPoint.getScriptBytes()));
        Assertions.assertEquals(confirmations, outPoint.getConfirmations());
        Assertions.assertEquals(value, outPoint.getValue().getValue());
    }
}
