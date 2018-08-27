package me.alb_i986.test.asserts.retry.internal;

import java.util.concurrent.TimeUnit;

public class Timeout {

    private final long timeout;
    private final TimeUnit timeoutUnit;

    private long startTimeNanos;

    public Timeout(long timeout, TimeUnit timeoutUnit) {
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        restart();
    }

    //TODO allow unit tests to inject a mocked Clock (with Joda time or jdk8)

    /**
     * Reset the timeout so that it starts over.
     */
    public void restart() {
        this.startTimeNanos = System.nanoTime();
    }

    /**
     * @return true if the timeout has expired since the last invocation to {@link #restart()}
     */
    public boolean isExpired() {
        return getElapsedTimeMillis() > timeoutUnit.toMillis(timeout);
    }

    public long getElapsedTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
    }
}
