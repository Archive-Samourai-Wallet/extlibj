package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FeeUtilTest {
  private static final FeeUtil feeUtil = FeeUtil.getInstance();

  @Test
  public void estimatedSizeSegwitCahoots() throws Exception {
    // ideally it should return 406, see 0edc06f566a615bf2f8cd9034744c41bcd282f702a9dbce17b21f06b2e75ad3f
    Assertions.assertEquals(422, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
  }

  @Test
  public void estimatedSizeSegwit() throws Exception {
    Assertions.assertEquals(113, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0));
    Assertions.assertEquals(193, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0));
    Assertions.assertEquals(276, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1));

    Assertions.assertEquals(319, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0));
    Assertions.assertEquals(422, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
    Assertions.assertEquals(525, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0));
  }

  @Test
  public void estimatedSizeSegwit_taproot() throws Exception {
    Assertions.assertEquals(113, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0));
    Assertions.assertEquals(113, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0, 0));
    Assertions.assertEquals(156, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 1, 0));

    Assertions.assertEquals(193, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0));
    Assertions.assertEquals(193, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0, 0));
    Assertions.assertEquals(236, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1,0));

    Assertions.assertEquals(276, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1));
    Assertions.assertEquals(276, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0,1));
    Assertions.assertEquals(319, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1, 1));

    Assertions.assertEquals(319, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0));
    Assertions.assertEquals(319, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0,0));
    Assertions.assertEquals(362, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 1,0));

    Assertions.assertEquals(422, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
    Assertions.assertEquals(422, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0, 0));
    Assertions.assertEquals(465, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 1, 0));

    Assertions.assertEquals(525, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0));
    Assertions.assertEquals(525, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0, 0));
    Assertions.assertEquals(568, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 1, 0));
  }

  @Test
  public void estimatedFeeSegwit() throws Exception {
    Assertions.assertEquals(118, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0));
    Assertions.assertEquals(113, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 1));
    Assertions.assertEquals(1130, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 10));
    Assertions.assertEquals(11300, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 100));

    Assertions.assertEquals(1930, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 10));
    Assertions.assertEquals(2760, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 1, 10));
    Assertions.assertEquals(3190, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 0, 10));
    Assertions.assertEquals(4220, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 0, 10));
    Assertions.assertEquals(5250, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 0, 10));
  }

  @Test
  public void estimatedFeeSegwit_taproot() throws Exception {
    Assertions.assertEquals(118, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 0));
    Assertions.assertEquals(128, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 0));
    Assertions.assertEquals(208, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 0));

    Assertions.assertEquals(113, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 1));
    Assertions.assertEquals(122, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 1));
    Assertions.assertEquals(199, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 1));

    Assertions.assertEquals(1130, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 10));
    Assertions.assertEquals(1220, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 10));
    Assertions.assertEquals(1990, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 10));

    Assertions.assertEquals(11300, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0, 100));
    Assertions.assertEquals(12200, feeUtil.estimatedFeeSegwit(0, 0, 1, 0, 1, 0, 100));
    Assertions.assertEquals(19900, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 2, 0, 100));

    Assertions.assertEquals(1930, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 0, 10));
    Assertions.assertEquals(2020, feeUtil.estimatedFeeSegwit(1, 0, 0, 0, 1, 0, 10));
    Assertions.assertEquals(2790, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 2, 0, 10));

    Assertions.assertEquals(2760, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 1, 10));
    Assertions.assertEquals(2850, feeUtil.estimatedFeeSegwit(1, 0, 0, 0, 1, 1, 10));
    Assertions.assertEquals(3620, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 2, 1, 10));

    Assertions.assertEquals(3190, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 0, 0, 10));
    Assertions.assertEquals(3460, feeUtil.estimatedFeeSegwit(0, 0, 3, 0, 3, 0, 10));
    Assertions.assertEquals(4050, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 2, 0, 10));

    Assertions.assertEquals(4220, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 0, 0, 10));
    Assertions.assertEquals(4580, feeUtil.estimatedFeeSegwit(0, 0, 4, 0, 4, 0, 10));
    Assertions.assertEquals(5080, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 2, 0, 10));

    Assertions.assertEquals(5250, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 0, 0, 10));
    Assertions.assertEquals(5700, feeUtil.estimatedFeeSegwit(0, 0, 5, 0, 5, 0, 10));
    Assertions.assertEquals(6110, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 2, 0, 10));
  }
}
