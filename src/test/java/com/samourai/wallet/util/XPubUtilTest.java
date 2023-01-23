package com.samourai.wallet.util;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.samourai.wallet.util.XPUB;

public class XPubUtilTest {
  private static final XPubUtil xPubUtil = XPubUtil.getInstance();
  private static final NetworkParameters params = TestNet3Params.get();

  private static final String XPUB = "vpub5YU16pCSZfdEqJDFzEZSFXMH7b6kjwSHNTqVUoJuWQw5kQ1Fk3iKQHzDqoJhDEEbHYKS6jAuDWcC74H1iFuqdAxtwV6KJybqqMKX4VYguj5";

  @Test
  public void getAddressSegwit() throws Exception {
    Assertions.assertEquals("2N7p4EYD5nFND7eEErWTsTNge2YbUWFd1vM", xPubUtil.getAddressSegwit(XPUB, 0, 0, params));
    Assertions.assertEquals("2N8q9YEErAFDBeqN67s6jacxmYi3JG8D2L1", xPubUtil.getAddressSegwit(XPUB, 1, 0, params));
    Assertions.assertEquals("2N61xXDmcwY5RygEbyM47KbcQVeYVzYB8U1", xPubUtil.getAddressSegwit(XPUB, 0, 1, params));
    Assertions.assertEquals("2NEEYKj25seycVENYHjAc1LrxW9YR8QMSH7", xPubUtil.getAddressSegwit(XPUB, 1, 1, params));
  }

  @Test
  public void getAddressBech32() throws Exception {
    Assertions.assertEquals("tb1q6m3urxjc8j2l8fltqj93jarmzn0975nnxuymnx", xPubUtil.getAddressBech32(XPUB, 0, 0, params));
    Assertions.assertEquals("tb1qcaerxclcmu9llc7ugh65hemqg6raaz4sul535f", xPubUtil.getAddressBech32(XPUB, 1, 0, params));
    Assertions.assertEquals("tb1qtyj9wey8mf3t79ltzpmsjcm5qkj2svmg5tznj5", xPubUtil.getAddressBech32(XPUB, 0, 1, params));
    Assertions.assertEquals("tb1qdcewkxujau042zva8xf3tgf5k6z6069838d8st", xPubUtil.getAddressBech32(XPUB, 1, 1, params));
  }

  @Test
  public void xpub() throws Exception {
  	// acount 0
  	XPUB xpub = new XPUB("zpub6rszzdAK6RuafeRwyN8z1cgWcXCuKbLmjjfnrW4fWKtcoXQ8787214pNJjnBG5UATyghuNzjn6Lfp5k5xymrLFJnCy46bMYJPyZsbpFGagT");
  	xpub.decode();
  	int child = xpub.getChild();
  	Assertions.assertEquals(child, -2147483648);

  	// acount 2147483646
  	xpub = new XPUB("zpub6rszzdATS6SYmnDsZFa7fx3sdFPYYKjyqoCETE1KuMK6fVdjcse9xobKhm5fUAYpcuk4U8RVMRsaPtA1UQKGFQExaojoqvdpTfeNoDiLpcg");
  	xpub.decode();
  	child = xpub.getChild();
  	Assertions.assertEquals(child, -2);
  }

}
