package com.samourai.wallet.util;

import com.samourai.wallet.test.AbstractTest;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncUtilTest extends AbstractTest {
  private int counter;

  // TIMEOUT
  private MutableInt countRuns;
  private Single<Long> single2sec;
  private Completable completable2sec;
  private Callable<Long> callable2sec;
  private long RESULT = 1L;
  private long SLEEP = 2000;
  private long TIMEOUT = 2100;

  @BeforeEach
  public void setUp() throws Exception{
    super.setUp();
    this.counter = 0;

    // TIMEOUT
    countRuns = new MutableInt(0);
    single2sec = Single.fromCallable(() -> {
      countRuns.increment();
      try {
        Thread.sleep(SLEEP);
      } catch (InterruptedException e) {}
      return RESULT;
    });

    completable2sec = Completable.fromRunnable(() -> {
      countRuns.increment();
      try {
        Thread.sleep(SLEEP);
      } catch (InterruptedException e) {}
    });

    callable2sec = () -> {
      countRuns.increment();
      try {
        Thread.sleep(SLEEP);
      } catch (InterruptedException e) {}
      return RESULT;
    };
  }

  @Test
  public void blockingGet_error() throws Exception {
    try {
      asyncUtil.blockingGet(Single.error(new IllegalArgumentException("test")));
      Assertions.assertTrue(false);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  @Test
  public void blockingGet_success() throws Exception {
    int result = asyncUtil.blockingGet(Single.just(123));
    Assertions.assertEquals(123, result);
  }

  @Test
  public void blockingLast_error() throws Exception {
    try {
      asyncUtil.blockingLast(Observable.error(new IllegalArgumentException("test")));
      Assertions.assertTrue(false);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  @Test
  public void blockingLast_success() throws Exception {
    Integer[] sources = new Integer[]{1,2,3};
    int result = asyncUtil.blockingLast(Observable.fromArray(sources));
    Assertions.assertEquals(3, result);
  }

  @Test
  public void blockingAwait_error() throws Exception {
    try {
      asyncUtil.blockingAwait(Completable.fromCallable(() -> {
        throw new IllegalArgumentException("test");
      }));
      Assertions.assertTrue(false);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  @Test
  public void runIOAsync_success() throws Exception {
      String result = asyncUtil.blockingGet(asyncUtil.runIOAsync(() -> "ok"));
      Assertions.assertEquals("ok", result);
  }

  @Test
  public void runIOAsync_error() throws Exception {
    try {
      asyncUtil.blockingGet(asyncUtil.runIOAsync(() -> {
        throw new IllegalArgumentException("test");
      }));
      Assertions.assertTrue(false);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  @Test
  public void runIOAsync() throws Exception {
    this.counter = 0;
    Callable callable = () -> counter++;

    // simple run
    asyncUtil.blockingGet(asyncUtil.runIOAsync(callable));
    Assertions.assertEquals(1, counter);

    // with doOnComplete
    asyncUtil.blockingGet(asyncUtil.runIOAsync(callable)
            .doOnSuccess(
                    v -> {
                      Assertions.assertEquals(2, counter);
                      counter++;
                    })
            .doOnSuccess(
                    v -> {
                      Assertions.assertEquals(3, counter);
                      counter++;
                    })
    );
    Assertions.assertEquals(4, counter);
  }

  @Test
  public void runIO_success() throws Exception {
    String result = asyncUtil.runIO(() -> "ok");
    Assertions.assertEquals("ok", result);
  }

  @Test
  public void runIO_error() throws Exception {
    try {
      asyncUtil.runIO(() -> {
        throw new IllegalArgumentException("test");
      });
      Assertions.assertTrue(false);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  // TIMEOUT

  @Test
  public void blockingGet_timeout_expire() throws Exception{
    // throws on timeout
    Assertions.assertThrows(TimeoutException.class, () -> {
      asyncUtil.blockingGet(single2sec, TIMEOUT/2);
    });
    Assertions.assertEquals(1, countRuns.intValue());
  }

  @Test
  public void blockingGet_timeout_success() throws Exception{
    // success before timeout
    Assertions.assertEquals(RESULT, asyncUtil.blockingGet(single2sec, TIMEOUT));
    Assertions.assertEquals(1, countRuns.intValue());
  }

  @Test
  public void blockingAwait_timeout_expire() throws Exception{
    // throws on timeout
    Assertions.assertThrows(TimeoutException.class, () -> {
      asyncUtil.blockingAwait(completable2sec, TIMEOUT/2);
    });
    Assertions.assertEquals(1, countRuns.intValue());
  }

  @Test
  public void blockingAwait_timeout_success() throws Exception{
    // success before timeout
    asyncUtil.blockingAwait(completable2sec, TIMEOUT);
    Assertions.assertEquals(1, countRuns.intValue());
  }

  @Test
  public void timeout_single() throws Exception {
    // success
    asyncUtil.blockingGet(asyncUtil.timeout(single2sec,3000));

    // timeout
    Assertions.assertThrows(TimeoutException.class, () ->
      asyncUtil.blockingGet(asyncUtil.timeout(single2sec,1000))
    );
  }

  @Test
  public void timeout_completable() throws Exception {
    // success
    asyncUtil.blockingAwait(asyncUtil.timeout(completable2sec,3000));

    // timeout
    Assertions.assertThrows(TimeoutException.class, () ->
            asyncUtil.blockingAwait(asyncUtil.timeout(completable2sec,1000))
    );
  }

  //

  @Test
  public void runAndRetry_timeout() throws Exception{
    // global timeout before end of loop
    Assertions.assertThrows(TimeoutException.class, () -> {
      asyncUtil.blockingGet(
              asyncUtil.runAndRetry(callable2sec, 500),1000);
    });
    Assertions.assertEquals(1, countRuns.intValue());
  }

  @Test
  public void runAndRetry_success() throws Exception{
    // success before timeout
    Assertions.assertEquals(RESULT,
            asyncUtil.blockingGet(
                    asyncUtil.runAndRetry(callable2sec, 3000), 4000));
    Assertions.assertEquals(1, countRuns.intValue());
  }
}
