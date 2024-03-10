package com.samourai.wallet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class LoopUntilSuccess<T> {
    private static final Logger log = LoggerFactory.getLogger(LoopUntilSuccess.class.getName());

    private Callable<Optional<T>> doLoop; // returns Optional.empty() to continue looping when no value found
    private long retryFrequencyMs;
    private Supplier<Boolean> isDoneOrNull;
    private String id;
    private long timeoutMs;
    private boolean done;
    
    public LoopUntilSuccess(Callable<Optional<T>> doLoop, long retryFrequencyMs, Supplier<Boolean> isDoneOrNull, long timeoutMs) {
        this.doLoop = doLoop;
        this.retryFrequencyMs = Math.min(retryFrequencyMs, timeoutMs);
        this.isDoneOrNull = isDoneOrNull;
        this.id = "loop-"+RandomUtil.getInstance().nextInt(1000);
        this.timeoutMs = timeoutMs;
        this.done = false;
    }

    public T run() throws Exception {
        long timeDone = System.currentTimeMillis()+timeoutMs;
        /*int cycle=0;
        if (log.isDebugEnabled()) {
            log.debug("START_LOOP_UNTIL_SUCCESS "+id);
        }*/
        while (true) {
            if (isDone()) {
                throw new InterruptedException("Loop ending with exit (done)");
            }
            if (System.currentTimeMillis()>timeDone) {
                throw new TimeoutException("Loop ending with timeout after "+timeoutMs+"ms");
            }
            /*if (log.isDebugEnabled()) {
                log.debug("CYCLE_LOOP_UNTIL_SUCCESS "+id+" "+cycle);
            }*/
            long loopStartTime = System.currentTimeMillis();
            try {
                // run loop (without timeout)
                Optional<T> opt = AsyncUtil.getInstance().blockingGet(
                        AsyncUtil.getInstance().runAsync(doLoop, retryFrequencyMs));
                if (opt.isPresent()) {
                    // value found
                    /*if (log.isDebugEnabled()) {
                        log.debug("EXIT_LOOP SUCCESS "+id+" #"+opt.get());
                    }*/
                    done = true;
                    return opt.get();
                }
                // if no value, continue looping
            } catch (TimeoutException e) {
                // continue looping
            }

            // wait delay before next loop
            long loopSpentTime = System.currentTimeMillis() - loopStartTime;
            long waitTime = retryFrequencyMs - loopSpentTime;
            //if (log.isDebugEnabled()) {
            //    log.debug("runWithTimeoutFrequency(): loop timed out, loopSpentTime=" + loopSpentTime + ", waitTime=" + waitTime);
            //}
            if (waitTime > 0) {
                synchronized (this) {
                    try {
                        wait(waitTime);
                    } catch (InterruptedException ee) {
                    }
                }
            }
            //cycle++;
        }
    }

    public boolean isDone() {
        return (done || (isDoneOrNull != null && isDoneOrNull.get()));
    }
}
