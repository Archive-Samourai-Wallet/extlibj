package com.samourai.wallet.util;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Callable;

public class AsyncUtil {
    private static AsyncUtil instance;

    public static AsyncUtil getInstance() {
        if(instance == null) {
            instance = new AsyncUtil();
        }
        return instance;
    }

    public <T> T blockingSingle(Observable<T> o) throws Exception {
        try {
            return o.blockingSingle();
        } catch (RuntimeException e) {
            // blockingSingle wraps errors with RuntimeException, unwrap it
            if (e.getCause() != null && e instanceof Exception) {
                throw (Exception)e.getCause();
            }
            throw e;
        }
    }

    public void blockingAwait(Completable o) throws Exception {
        try {
            o.blockingAwait();
        } catch (RuntimeException e) {
            // blockingAwait wraps errors with RuntimeException, unwrap it
            if (e.getCause() != null && e instanceof Exception) {
                throw (Exception)e.getCause();
            }
            throw e;
        }
    }

    public <T> Observable<T> runIOAsync(final Callable<T> callable) {
        return Observable.fromCallable(() -> callable.call()).subscribeOn(Schedulers.io());
    }

    public Completable runIOAsyncCompletable(final Action action) {
        return Completable.fromAction(() -> action.run()).subscribeOn(Schedulers.io());
    }

    public <T> T runIO(final Callable<T> callable) throws Exception {
        return blockingSingle(runIOAsync(callable));
    }

    public void runIO(final Action action) throws Exception {
        blockingAwait(runIOAsyncCompletable(action));
    }
}
