package com.samourai.wallet.client;

import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class KeyBagTest extends AbstractTest {
  private KeyBag keyBag;

  public KeyBagTest() throws Exception {
    super();
  }

  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();

    keyBag = new KeyBag();
  }

  @Test
  public void add() throws Exception {
    BipWallet bipWallet = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);
    UnspentOutput utxo1 = utxoProvider.addUtxo(bipWallet, 1111).toUnspentOutputs().iterator().next();
    UnspentOutput utxo2 = utxoProvider.addUtxo(bipWallet, 2222).toUnspentOutputs().iterator().next();
    UnspentOutput utxo3 = utxoProvider.addUtxo(bipWallet, 3333).toUnspentOutputs().iterator().next();
    byte[] key1 = bipWallet.getAddressAt(utxo1).getHdAddress().getECKey().getPrivKeyBytes();
    byte[] key2 = bipWallet.getAddressAt(utxo2).getHdAddress().getECKey().getPrivKeyBytes();
    keyBag.add(utxo1, walletSupplier);
    keyBag.add(utxo2, walletSupplier);

    // verify
    Assertions.assertArrayEquals(key1, keyBag.getPrivKeyBytes(utxo1));
    Assertions.assertArrayEquals(key2, keyBag.getPrivKeyBytes(utxo2));
    Assertions.assertNull(keyBag.getPrivKeyBytes(utxo3));
  }

  @Test
  public void addAll() throws Exception {
    BipWallet bipWallet = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84);
    UnspentOutput utxo1 = utxoProvider.addUtxo(bipWallet, 1111).toUnspentOutputs().iterator().next();
    UnspentOutput utxo2 = utxoProvider.addUtxo(bipWallet, 2222).toUnspentOutputs().iterator().next();
    UnspentOutput utxo3 = utxoProvider.addUtxo(bipWallet, 3333).toUnspentOutputs().iterator().next();
    byte[] key1 = bipWallet.getAddressAt(utxo1).getHdAddress().getECKey().getPrivKeyBytes();
    byte[] key2 = bipWallet.getAddressAt(utxo2).getHdAddress().getECKey().getPrivKeyBytes();
    keyBag.addAll(Arrays.asList(utxo1, utxo2), walletSupplier);

    // verify
    Assertions.assertArrayEquals(key1, keyBag.getPrivKeyBytes(utxo1));
    Assertions.assertArrayEquals(key2, keyBag.getPrivKeyBytes(utxo2));
    Assertions.assertNull(keyBag.getPrivKeyBytes(utxo3));
  }
}