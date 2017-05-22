package me.alb_i986.testing.hamcrest.assertion.retry.internal;

/**
 * Thrown by {@link RetryAssertEngine} when a retry assertion fails.
 */
public class RetryAssertionError extends AssertionError {

    public RetryAssertionError(String message) {
        super(message);
    }

    public RetryAssertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
