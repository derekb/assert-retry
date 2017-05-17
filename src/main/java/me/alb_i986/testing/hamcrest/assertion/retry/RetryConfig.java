package me.alb_i986.testing.hamcrest.assertion.retry;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.hamcrest.assertion.AssertRetry;

/**
 * Configuration for {@link AssertRetry} methods.
 * <p>
 * Immutable object: use {@link #builder()} to start building an instance;
 * use {@link #DEFAULT_CONFIG} to get an instance pre-configured with the {@link DefaultValues default values}.
 *
 * @author Alberto Scotto
 */
public class RetryConfig {

    /**
     * A config with the default values:
     * <ul>
     *     <li>{@link DefaultValues#MAX_RETRY_TIMES}</li>
     *     <li>{@link DefaultValues#WAIT_STRATEGY}</li>
     *     <li>{@link DefaultValues#RETRY_ON_EXCEPTION}</li>
     * </ul>
     */
    public static final RetryConfig DEFAULT_CONFIG = builder().build();

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
         * By default, retry only once (i.e. max 2 executions in total).
         */
        public static final int MAX_RETRY_TIMES = 1;

        private DefaultValues() {}
    }

    /**
     * Gives access to the Builder API, which allows to configure and return an instance of {@link RetryConfig}.
     *
     * @see Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private boolean retryOnException;
    private int maxRetryTimes;
    private Runnable waitStrategy;

    protected RetryConfig() {
    }

    /**
     * Whether we should retry when the supplier of actual values throws an exception.
     */
    public boolean isRetryOnException() {
        return retryOnException;
    }

    /**
     * How many times to retry for.
     * The max total number of executions is {@code maxRetryTimes + 1}.
     */
    // TODO change to getMaxAttempts, as to simplify explanations in javadoc (no more "maxretrytimes + 1" bullsh*t)
    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    /**
     * The strategy to use to wait between attempts,
     * e.g. sleep, or wait for something to happen.
     *
     * @see WaitStrategies
     */
    public Runnable getWaitStrategy() {
        return waitStrategy;
    }

    /**
     * Builds immutable instances of {@link RetryConfig}.
     * <p>
     * Parameters which have not been set explicitly, will be set to their default value,
     * as defined in {@link DefaultValues}.
     *
     * @see DefaultValues
     */
    public static class Builder {

        private Runnable waitStrategy;
        private Boolean retryOnException;
        private Integer maxRetryTimes;

        /**
         * Access is only allowed to {@link RetryConfig#builder()}.
         */
        private Builder() {
        }

        /**
         * @throws IllegalArgumentException in case of null arguments
         *
         * @see RetryConfig#getWaitStrategy()
         */
        public Builder withWaitStrategy(Runnable waitStrategy) {
            if (waitStrategy == null) {
                throw new IllegalArgumentException("null strategy");
            }
            this.waitStrategy = waitStrategy;
            return this;
        }

        /**
         * @see RetryConfig#isRetryOnException()
         */
        public Builder withRetryOnException(boolean retryOnException) {
            this.retryOnException = retryOnException;
            return this;
        }

        /**
         * @throws IllegalArgumentException in case of negative arguments
         *
         * @see RetryConfig#getMaxRetryTimes()
         */
        public Builder withMaxRetryTimes(int maxRetryTimes) {
            if (maxRetryTimes < 0) {
                throw new IllegalArgumentException("negative integer");
            }
            this.maxRetryTimes = maxRetryTimes;
            return this;
        }

        /**
         * Creates and returns an instance, configured according to the previous calls to the setters,
         * setting the parameters to the {@link DefaultValues} if not explicitly set.
         *
         * @return a configured instance of {@link RetryConfig}
         */
        public RetryConfig build() {
            RetryConfig retryConfig = new RetryConfig();
            retryConfig.waitStrategy = waitStrategy == null ? DefaultValues.WAIT_STRATEGY : waitStrategy;
            retryConfig.retryOnException = retryOnException == null ? DefaultValues.RETRY_ON_EXCEPTION : retryOnException;
            retryConfig.maxRetryTimes = maxRetryTimes == null ? DefaultValues.MAX_RETRY_TIMES : maxRetryTimes;
            return retryConfig;
        }
    }
}