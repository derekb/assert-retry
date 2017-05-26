package me.alb_i986.testing.assertions.retry;

import java.util.concurrent.TimeUnit;

/**
 * Factory methods of wait strategies to feed into {@link RetryConfigBuilder#waitStrategy(Runnable)}.
 * <p>
 * The most common wait strategy is {@link #sleep(long, TimeUnit)}.
 * More advanced usages may wait for a specific event to occur, e.g. for a message to be published on a queue.
 *
 * <p>Please note: when defining custom strategies, please override {@code toString()} to return
 * a meaningful description of the strategy, e.g. "waiting for a message to be published on the queue myQueue".
 *
 * @author Alberto Scotto
 */
public class WaitStrategies {

    protected WaitStrategies() {
        // static class
    }

    /**
     * Sleep for the given amount of time.
     *
     * @throws IllegalArgumentException if the argument is negative
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
