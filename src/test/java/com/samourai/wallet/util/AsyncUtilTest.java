package com.samourai.wallet.util;

import com.samourai.wallet.api.backend.beans.HttpException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AsyncUtilTest {
  private static final FeeUtil feeUtil = FeeUtil.getInstance();

  @Test
  public void blockingSingle() throws Exception {
    try {
      AsyncUtil.blockingSingle(Observable.error(new HttpException("test", null)));
      Assertions.assertTrue(false);
    } catch (HttpException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }

  @Test
  public void blockingAwait() throws Exception {
    try {
      AsyncUtil.blockingAwait(Completable.fromCallable(() -> {
        throw new HttpException("test", null);
      }));
      Assertions.assertTrue(false);
    } catch (HttpException e) {
      Assertions.assertEquals("test",e.getMessage());
    }
  }
}
