package com.samourai.wallet.bipFormat;

import com.samourai.wallet.test.AbstractTest;
import org.bitcoinj.core.TransactionOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BipFormatSupplierImplTest extends AbstractTest {

  private BipFormatSupplierImpl bipFormatSupplier;

  public BipFormatSupplierImplTest() throws Exception {
    bipFormatSupplier = (BipFormatSupplierImpl)BIP_FORMAT.PROVIDER;
  }

  @Test
  public void findByAddress() throws Exception {
    Assertions.assertEquals(BIP_FORMAT.LEGACY, bipFormatSupplier.findByAddress("mn9QhsFiX2eEXtF6zrGn5N49iS8BHXFjBt", params));

    Assertions.assertEquals(BIP_FORMAT.SEGWIT_COMPAT, bipFormatSupplier.findByAddress("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF", params));

    Assertions.assertEquals(BIP_FORMAT.SEGWIT_NATIVE, bipFormatSupplier.findByAddress("tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4", params));

    Assertions.assertEquals(BIP_FORMAT.TAPROOT, bipFormatSupplier.findByAddress("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7", params));
    Assertions.assertEquals(BIP_FORMAT.TAPROOT, bipFormatSupplier.findByAddress("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy", params));
    Assertions.assertEquals(BIP_FORMAT.TAPROOT, bipFormatSupplier.findByAddress("tb1pqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesf3hn0c", params));
  }

  @Test
  public void getList() throws Exception {
    Assertions.assertEquals(4, bipFormatSupplier.getList().size());
  }

  @Test
  public void findById() throws Exception {
    Assertions.assertEquals(BIP_FORMAT.LEGACY, bipFormatSupplier.findById(BIP_FORMAT.LEGACY.getId()));
    Assertions.assertEquals(BIP_FORMAT.SEGWIT_COMPAT, bipFormatSupplier.findById(BIP_FORMAT.SEGWIT_COMPAT.getId()));
    Assertions.assertEquals(BIP_FORMAT.SEGWIT_NATIVE, bipFormatSupplier.findById(BIP_FORMAT.SEGWIT_NATIVE.getId()));
    Assertions.assertEquals(BIP_FORMAT.TAPROOT, bipFormatSupplier.findById(BIP_FORMAT.TAPROOT.getId()));
  }

  @Test
  public void getTransactionOutput_and_getToAddress() throws Exception {
    // LEGACY
    String address = "mn9QhsFiX2eEXtF6zrGn5N49iS8BHXFjBt";
    TransactionOutput txOutput = bipFormatSupplier.getTransactionOutput(address, 4000, params);
    Assertions.assertEquals(address, bipFormatSupplier.getToAddress(txOutput));

    // SEGWIT_COMPAT
    address = "2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF";
    txOutput = bipFormatSupplier.getTransactionOutput(address, 4000, params);
    Assertions.assertEquals(address, bipFormatSupplier.getToAddress(txOutput));

    // SEGWIT_NATIVE
    address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
    txOutput = bipFormatSupplier.getTransactionOutput(address, 4000, params);
    Assertions.assertEquals(address, bipFormatSupplier.getToAddress(txOutput));

    // P2TR
    address = "tb1pqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesf3hn0c";
    txOutput = bipFormatSupplier.getTransactionOutput(address, 4000, params);
    Assertions.assertEquals(address, bipFormatSupplier.getToAddress(txOutput));
  }
}
