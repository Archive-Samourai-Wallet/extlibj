package com.samourai.wallet.util;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class AsyncUtilTest {
  private static final AsyncUtil asyncUtil = AsyncUtil.getInstance();
  private int counter;

  @BeforeEach
  public void setUp() {
    this.counter = 0;
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
}
