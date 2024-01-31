package com.samourai.wallet.util;

import com.samourai.wallet.test.AbstractTest;
import io.reactivex.Single;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class ThreadUtilTest extends AbstractTest {
    private MutableInt countRuns;
    private Callable<Long> callable2sec;
    private Runnable runnable2sec;

    private long RESULT = 1L;
    private long TIMEOUT = 2100;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        countRuns = new MutableInt(0);
        callable2sec = () -> {
            runnable2sec.run();
            return RESULT;
        };
        runnable2sec = () -> {
            countRuns.increment();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
        };
    }

    @Test
    public void runWithTimeout_callable_timeout() throws Exception{
        // throws on timeout
        Assertions.assertThrows(TimeoutException.class, () -> {
            asyncUtil.blockingGet(threadUtil.runWithTimeout(callable2sec, TIMEOUT/2));
        });
        Assertions.assertEquals(1, countRuns.intValue());
    }

    @Test
    public void runWithTimeout_callable_success() throws Exception{
        // success before timeout
        Assertions.assertEquals(RESULT, asyncUtil.blockingGet(threadUtil.runWithTimeout(callable2sec, TIMEOUT)));
        Assertions.assertEquals(1, countRuns.intValue());
    }

    @Test
    public void runWithTimeout_runnable_timeout() throws Exception{
        // throws on timeout
        Assertions.assertThrows(TimeoutException.class, () -> {
            asyncUtil.blockingAwait(threadUtil.runWithTimeout(runnable2sec, TIMEOUT/2));
        });
        Assertions.assertEquals(1, countRuns.intValue());
    }

    @Test
    public void runWithTimeout_runnable_success() throws Exception{
        // success before timeout
        asyncUtil.blockingAwait(threadUtil.runWithTimeout(runnable2sec, TIMEOUT));
        Assertions.assertEquals(1, countRuns.intValue());
    }

    //
/*
    @Test
    public void runWithTimeoutAndRetry_timeout() throws Exception{
        // each loop gets stopped by timeout before completing
        // then, global result throws on global timeout
        Assertions.assertThrows(TimeoutException.class, () -> {
            asyncUtil.blockingGet(
                    threadUtil.runWithTimeoutAndRetry(callable2sec, 500), TIMEOUT);
        });
        Assertions.assertEquals(5, countRuns.intValue());
    }

    @Test
    public void runWithTimeoutAndRetry_abort() throws Exception{
        // each loop gets stopped by timeout before completing
        // then, global result throws on global timeout
        Assertions.assertThrows(InterruptedException.class, () -> {
            asyncUtil.blockingGet(
                    threadUtil.runWithTimeoutAndRetry(callable2sec, 500,
                            () -> countRuns.intValue()>=2),TIMEOUT);
        });
        Assertions.assertEquals(2, countRuns.intValue());
    }

    @Test
    public void runWithTimeoutAndRetry_success() throws Exception{
        // success before timeout
        Assertions.assertEquals(RESULT,
                asyncUtil.blockingGet(
                        threadUtil.runWithTimeoutAndRetry(callable2sec, 3000),4000));
        Assertions.assertEquals(1, countRuns.intValue());
    }*/
}
