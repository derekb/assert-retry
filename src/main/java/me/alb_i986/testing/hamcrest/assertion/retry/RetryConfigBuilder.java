package me.alb_i986.testing.hamcrest.assertion.retry;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.hamcrest.assertion.retry.internal.RetryConfig;

/**
 * Allows to configure an instance of {@link RetryConfig}.
 * <p>
 * Parameters which have not been set explicitly, will be set to their default value,
 * as defined in {@link DefaultValues}.
 */
public class RetryConfigBuilder {

    /**
     * A config with the default values:
     * <ul>
     *     <li>{@link DefaultValues#MAX_ATTEMPTS}</li>
     *     <li>{@link DefaultValues#WAIT_STRATEGY}</li>
     *     <li>{@link DefaultValues#RETRY_ON_EXCEPTION}</li>
     * </ul>
     */
    public static final RetryConfig DEFAULT_CONFIG = new RetryConfigBuilder().build();

    private Runnable waitStrategy;
    private Boolean retryOnException;
    private Integer maxAttempts;

    /**
     * Access for end users is only allowed from {@link AssertRetry#configureRetry()}.
     */
    protected RetryConfigBuilder() {
    }

    /**
     * Configures the strategy to use to wait between attempts,
     * e.g. {@link WaitStrategies#sleep(long, TimeUnit) sleep},
     * or a custom "wait for X to happen".
     *
     * @throws IllegalArgumentException in case of null arguments
     *
     * @see WaitStrategies
     */
    public RetryConfigBuilder waitStrategy(Runnable waitStrategy) {
        if (waitStrategy == null) {
            throw new IllegalArgumentException("null strategy");
        }
        this.waitStrategy = waitStrategy;
        return this;
    }

    /**
     * Whether we should retry when the supplier of actual values throws an exception.
     */
    public RetryConfigBuilder retryOnException(boolean retryOnException) {
        this.retryOnException = retryOnException;
        return this;
    }

    /**
     * How many times to run the assertion for, in case it doesn't pass.
     *
     * @throws IllegalArgumentException in case of arguments < 1
     */
    public RetryConfigBuilder maxAttempts(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("integer < 1");
        }
        this.maxAttempts = maxAttempts;
        return this;
    }

    /**
     * Creates and returns an instance, configured according to the previous calls to the
     * setter methods, setting the parameters to the {@link DefaultValues} if not explicitly set.
     *
     * @return a configured instance of {@link RetryConfig}
     */
    public RetryConfig build() {
        Runnable waitStrategy = this.waitStrategy == null ? DefaultValues.WAIT_STRATEGY : this.waitStrategy;
        boolean retryOnException = this.retryOnException == null ? DefaultValues.RETRY_ON_EXCEPTION : this.retryOnException;
        int maxAttempts = this.maxAttempts == null ? DefaultValues.MAX_ATTEMPTS : this.maxAttempts;
        return new RetryConfig(maxAttempts, waitStrategy, retryOnException);
    }

    public static class DefaultValues {

        /**
         * By default, sleep for 1 second.
         */
        public static final Runnable WAIT_STRATEGY = WaitStrategies.sleep(1, TimeUnit.SECONDS);

        /**
         * By default, do <i>not</i> retry if the supplier throws an exception.
         */
        public static final boolean RETRY_ON_EXCEPTION = false;

        /**
         * By default, retry only once, i.e. max 2 executions in total.
         */
        public static final int MAX_ATTEMPTS = 2;

        private DefaultValues() {}
    }
}
