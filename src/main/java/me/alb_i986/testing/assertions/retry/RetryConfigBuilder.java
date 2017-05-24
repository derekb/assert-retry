package me.alb_i986.testing.assertions.retry;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.assertions.AssertRetry;
import me.alb_i986.testing.assertions.retry.internal.RetryConfig;

/**
 * Provides a fluent DSL allowing to configure the retry mechanism by building
 * an instance of {@link RetryConfig}.
 * <p>
 * For each parameter a setter method is provided.
 * If a parameter has not been set explicitly, it will be set to its default value
 * as defined in {@link DefaultValues}.
 */
public class RetryConfigBuilder {

    private Runnable waitStrategy;
    private Boolean retryOnException;
    private Integer maxAttempts;

    /**
     * @deprecated end users should rather rely on {@link AssertRetry#configureRetry()}.
     */
    @Deprecated // to give a hint to end users about the correct usage of our API
    public RetryConfigBuilder() {
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
     * How many times to run the assertion for, in case it fails.
     *
     * @throws IllegalArgumentException if maxAttempts is < 1
     */
    public RetryConfigBuilder maxAttempts(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts < 1");
        }
        this.maxAttempts = maxAttempts;
        return this;
    }

    /**
     * Creates and returns an instance of {@link RetryConfig},
     * configured according to the previous calls to the setter methods.
     * In case a parameter has not been explicitly set, it will be set to its default value,
     * as defined in {@link DefaultValues}.
     *
     * @return a configured instance of {@link RetryConfig}
     */
    public RetryConfig build() {
        Runnable waitStrategy = this.waitStrategy == null ? DefaultValues.WAIT_STRATEGY : this.waitStrategy;
        boolean retryOnException = this.retryOnException == null ? DefaultValues.RETRY_ON_EXCEPTION : this.retryOnException;
        int maxAttempts = this.maxAttempts == null ? DefaultValues.MAX_ATTEMPTS : this.maxAttempts;
        return new RetryConfig(maxAttempts, waitStrategy, retryOnException);
    }

    /**
     * Defines the default values:
     * <ul>
     *     <li>{@link DefaultValues#MAX_ATTEMPTS}</li>
     *     <li>{@link DefaultValues#WAIT_STRATEGY}</li>
     *     <li>{@link DefaultValues#RETRY_ON_EXCEPTION}</li>
     * </ul>
     */
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
