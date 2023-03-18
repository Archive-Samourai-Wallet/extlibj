package com.samourai.wallet.util;

import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomUtilTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(RandomUtilTest.class);

    @Test
    public void nextBytes() {
        int len = 10;
        byte[] res = RandomUtil.getInstance().nextBytes(len);
        Assertions.assertArrayEquals(new byte[len], res);
    }

    @Test
    public void randomInt() {
        int res = RandomUtil.random(2,10);
        Assertions.assertEquals(2, res);
    }

    @Test
    public void randomLong() {
        long res = RandomUtil.random(2L,10L);
        Assertions.assertEquals(2, res);
    }

    @Test
    public void nextInt() {
        int res = RandomUtil.getInstance().nextInt(10);
        Assertions.assertEquals(0, res);
    }

    @Test
    public void nextLong() {
        long res = RandomUtil.getInstance().nextLong();
        Assertions.assertEquals(0, res);
    }
}
