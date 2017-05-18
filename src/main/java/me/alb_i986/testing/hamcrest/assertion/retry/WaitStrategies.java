package me.alb_i986.testing.hamcrest.assertion.retry;

import java.util.concurrent.TimeUnit;

/**
 * Factory methods of wait strategies to feed into {@link RetryConfig.Builder#withWaitStrategy(Runnable)}.
 *
 * <p>Please note: when defining custom strategies, please override {@code toString()} to return
 * a meaningful description of the strategy, e.g. "waiting for a message to be published to the queue myQueue".
 *
 * @author Alberto Scotto
 */
public class WaitStrategies {

    protected WaitStrategies() {
        // static class
    }

    /**
     * @throws IllegalArgumentException if the integer is negative
     *
     * @see Thread#sleep(long)
     */
    public static Runnable sleep(long time, TimeUnit timeUnit) {
        if (time <= 0) {
            throw new IllegalArgumentException("negative timeout");
        }
        return sleep(timeUnit.toMillis(time));
    }

    private static Runnable sleep(final long millis) {
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
