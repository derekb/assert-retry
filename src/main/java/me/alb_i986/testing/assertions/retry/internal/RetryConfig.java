package me.alb_i986.testing.assertions.retry.internal;

import me.alb_i986.testing.assertions.AssertRetry;
import me.alb_i986.testing.assertions.retry.RetryConfigBuilder;

/**
 * Holds the configuration parameters for {@link AssertRetry} methods. Immutable object.
 * <p>
 * Use {@link RetryConfigBuilder} to build an instance.
 * Use {@link RetryConfigBuilder#DEFAULT_CONFIG} to get an instance pre-configured
 * with the {@link RetryConfigBuilder.DefaultValues default values}.
 */
public class RetryConfig {

    private final boolean retryOnException;
    private final int maxAttempts;
    private final Runnable waitStrategy;

    public RetryConfig(int maxAttempts, Runnable waitStrategy, boolean retryOnException) {
        this.maxAttempts = maxAttempts;
        this.waitStrategy = waitStrategy;
        this.retryOnException = retryOnException;
    }

    /**
     * @see RetryConfigBuilder#retryOnException(boolean)
     */
    public boolean isRetryOnException() {
        return retryOnException;
    }

    /**
     * @see RetryConfigBuilder#maxAttempts(int)
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * @see RetryConfigBuilder#waitStrategy(Runnable)
     */
    public Runnable getWaitStrategy() {
        return waitStrategy;
    }

}
