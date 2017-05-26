package me.alb_i986.testing.assertions.retry.internal;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.assertions.retry.RetryConfigBuilder;

/**
 * Factory methods of wait strategies to be fed into {@link RetryConfigBuilder#waitStrategy(Runnable)}.
 */
public class WaitStrategies {

    protected WaitStrategies() {
        // static class
    }

    public static Runnable sleep(long time, TimeUnit timeUnit) {
        return sleep(timeUnit.toMillis(time));
    }

    public static Runnable sleep(final long millis) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    // swallow!
                }
            }

            @Override
            public String toString() {
                //TODO improve display of time: if t>1000ms, we should talk about seconds; if t>60s => minutes, etc.
                return "sleep for " + millis + "ms";
            }
        };
    }
}
