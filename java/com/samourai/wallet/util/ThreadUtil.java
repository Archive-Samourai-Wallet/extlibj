package com.samourai.wallet.util;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

public class ThreadUtil {
    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);
    private static ThreadUtil instance;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    public static ThreadUtil getInstance() {
        if (instance == null) {
            instance = new ThreadUtil();
        }
        return instance;
    }

    public <T> Single<T> runWithTimeout(Future<T> future, long timeoutMs) {
        return Single.fromFuture(future, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public <T> Single<T> runWithTimeout(Callable<T> callable, long timeoutMs) {
        Future<T> future = executorService.submit(callable);
        return runWithTimeout(future, timeoutMs);
    }

    public Completable runWithTimeout(Runnable runnable, long timeoutMs) {
        Callable<Optional> callable = () -> {
            runnable.run();
            return Optional.empty(); // must return non-null object
        };
        return Completable.fromSingle(runWithTimeout(callable, timeoutMs));
    }

    /**
     * Run loop (with timeout) every <loopFrequencyMs>
     *//*
    public <T> Single<T> runWithTimeoutAndRetry(
            Callable<T> doLoop, long retryFrequencyMs, Supplier<Boolean> isDoneOrNull) throws Exception {
        return Single.fromCallable(() -> {
            while (true) {
                if (isDoneOrNull != null && isDoneOrNull.get()) {
                    throw new InterruptedException("exit (done)");
                }
                long loopStartTime = System.currentTimeMillis();
                try {
                    // run loop with timeout
                    return asyncUtil.blockingGet(runWithTimeout(doLoop, retryFrequencyMs));
                } catch (TimeoutException e) {
                    // continue looping
                    long loopSpentTime = System.currentTimeMillis() - loopStartTime;
                    long waitTime = retryFrequencyMs - loopSpentTime;
                    if (log.isDebugEnabled()) {
                        log.debug("runWithTimeoutFrequency(): loop timed out, loopSpentTime=" + loopSpentTime + ", waitTime=" + waitTime);
                    }
                    if (waitTime > 0) {
                        synchronized (this) {
                            try {
                                wait(waitTime);
                            } catch (InterruptedException ee) {
                            }
                        }
                    }
                }
            }
        });
    }

    public <T> Single<T> runWithTimeoutAndRetry(Callable<T> doLoop, long retryFrequencyMs)throws Exception {
        return runWithTimeoutAndRetry(doLoop, retryFrequencyMs, null);
    }*/

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
