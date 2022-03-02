package com.samourai.wallet.util;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class AsyncUtil {
    public static <T> T blockingSingle(Observable<T> o) throws Exception {
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
    public static void blockingAwait(Completable o) throws Exception {
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
}
