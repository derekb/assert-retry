package me.alb_i986.test.asserts.retry.internal;

/**
 * Thrown by {@link AssertRetryEngine} when a retry assertion fails.
 */
public class RetryAssertionError extends AssertionError {

    public RetryAssertionError(String message) {
        super(message);
    }

    public RetryAssertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
