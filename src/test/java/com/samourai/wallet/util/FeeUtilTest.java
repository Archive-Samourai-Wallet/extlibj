package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FeeUtilTest {
  private static final FeeUtil feeUtil = FeeUtil.getInstance();

  @Test
  public void estimatedSizeSegwitCahoots() throws Exception {
    // ideally it should return 406, see 0edc06f566a615bf2f8cd9034744c41bcd282f702a9dbce17b21f06b2e75ad3f
    Assertions.assertEquals(418, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
  }

  @Test
  public void estimatedSizeSegwit() throws Exception {
    Assertions.assertEquals(112, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0));
    Assertions.assertEquals(192, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0));
    Assertions.assertEquals(272, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1));

    Assertions.assertEquals(316, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0));
    Assertions.assertEquals(418, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
    Assertions.assertEquals(520, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0));
  }

  @Test
  public void estimatedSizeSegwit_taproot() throws Exception {
    Assertions.assertEquals(112, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0));
    Assertions.assertEquals(112, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0, 0));
    Assertions.assertEquals(155, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 1, 0));

    Assertions.assertEquals(192, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0));
    Assertions.assertEquals(192, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0, 0));
    Assertions.assertEquals(235, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1,0));

    Assertions.assertEquals(272, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1));
    Assertions.assertEquals(272, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0,1));
    Assertions.assertEquals(315, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1, 1));

    Assertions.assertEquals(316, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0));
    Assertions.assertEquals(316, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0,0));
    Assertions.assertEquals(359, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 1,0));

    Assertions.assertEquals(418, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
    Assertions.assertEquals(418, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0, 0));
    Assertions.assertEquals(461, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 1, 0));

    Assertions.assertEquals(520, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0));
    Assertions.assertEquals(520, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0, 0));
    Assertions.assertEquals(563, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 1, 0));
  }

  @Test
  public void estimatedFeeSegwit() throws Exception {
    Assertions.assertEquals(117, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0));
    Assertions.assertEquals(112, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 1));
    Assertions.assertEquals(1120, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 10));
    Assertions.assertEquals(11200, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 100));

    Assertions.assertEquals(1920, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 10));
    Assertions.assertEquals(2720, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 1, 10));
    Assertions.assertEquals(3160, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 0, 10));
    Assertions.assertEquals(4180, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 0, 10));
    Assertions.assertEquals(5200, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 0, 10));
  }

  @Test
  public void estimatedFeeSegwit_taproot() throws Exception {
    Assertions.assertEquals(117, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 0));
    Assertions.assertEquals(128, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 0));
    Assertions.assertEquals(207, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 0));

    Assertions.assertEquals(112, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 1));
    Assertions.assertEquals(122, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 1));
    Assertions.assertEquals(198, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 1));

    Assertions.assertEquals(1120, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 10));
    Assertions.assertEquals(1220, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 10));
    Assertions.assertEquals(1980, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 10));

    Assertions.assertEquals(11200, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 100));
    Assertions.assertEquals(12200, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 100));
    Assertions.assertEquals(19800, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 100));

    Assertions.assertEquals(1920, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 0, 10));
    Assertions.assertEquals(2020, feeUtil.estimatedFeeSegwit(1, 0, 0, 0, 1, 0, 10));
    Assertions.assertEquals(2780, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 2, 0, 10));

    Assertions.assertEquals(2720, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 1, 10));
    Assertions.assertEquals(2820, feeUtil.estimatedFeeSegwit(1, 0, 0, 0, 1, 1, 10));
    Assertions.assertEquals(3580, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 2, 1, 10));

    Assertions.assertEquals(3160, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 0, 0, 10));
    Assertions.assertEquals(3460, feeUtil.estimatedFeeSegwit(0, 0, 3, 0, 3, 0, 10));
    Assertions.assertEquals(4020, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 2, 0, 10));

    Assertions.assertEquals(4180, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 0, 0, 10));
    Assertions.assertEquals(4580, feeUtil.estimatedFeeSegwit(0, 0, 4, 0, 4, 0, 10));
    Assertions.assertEquals(5040, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 2, 0, 10));

    Assertions.assertEquals(5200, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 0, 0, 10));
    Assertions.assertEquals(5700, feeUtil.estimatedFeeSegwit(0, 0, 5, 0, 5, 0, 10));
    Assertions.assertEquals(6060, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 2, 0, 10));
  }
}
