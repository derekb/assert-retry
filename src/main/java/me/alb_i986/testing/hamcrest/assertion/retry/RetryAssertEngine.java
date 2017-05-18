package me.alb_i986.testing.hamcrest.assertion.retry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.alb_i986.testing.hamcrest.assertion.RetryAssert;

/**
 * The core implementation of the {@link RetryAssert} methods.
 * Designed to be easily testable.
 * End users should not depend on this class.
 *
 * @see RetryAssert
 */
public class RetryAssertEngine {

    private static final Logger LOG = Logger.getLogger(RetryAssert.class.getName());

    private final RetryConfig retryConfig;

    public RetryAssertEngine(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public <T> T assertThat(Supplier<T> actualValuesSupplier, Matcher<T> matcher) {
        return assertThat("", actualValuesSupplier, matcher);
    }

    public <T> T assertThat(String failureReason, Supplier<T> actualValuesSupplier, Matcher<? super T> matcher) {
        List<T> suppliedValues = new ArrayList<>();
        long startTimeMillis = System.currentTimeMillis();
        int i;
        for (i = 1; i <= retryConfig.getMaxAttempts(); i++) { // i starts from 1
            if (i > 1) {
                retryConfig.getWaitStrategy().run(); // wait and then re-try
            }

            T actual;
            try {
                actual = actualValuesSupplier.get();
                suppliedValues.add(actual);
            } catch (Exception | AssertionError e) {
                if (!retryConfig.isRetryOnException()) {
                    throw new RetryAssertionError(String.format("Assertion failed after %d/%d attempts: " +
                            "the supplier of actual values failed", i, retryConfig.getMaxAttempts()), e);
                }

                // TODO configurable type of exception to retry on
//                if (!retryConfig.getRetryException().isAssignableFrom(e.getClass())) {
//                    throw e;
//                }
                LOG.log(Level.INFO, String.format("Supplier of actual values failed (%d/%d). Waiting before trying again: %s.",
                        i, retryConfig.getMaxAttempts(), retryConfig.getWaitStrategy()), e);
                continue;
            }
            try {
                org.junit.Assert.assertThat(actual, matcher);
                return actual; // assertion PASSED!
            } catch (AssertionError e) {
                LOG.log(Level.INFO, String.format("Assertion failed (%d/%d). Waiting before trying again: %s. %s",
                        i, retryConfig.getMaxAttempts(), retryConfig.getWaitStrategy(), e.toString()));
            }
        }

        // the assertion never passed => throw

        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        Description description = new StringDescription()
                .appendText(String.format("Assertion failed after %d/%d attempts ", i - 1, retryConfig.getMaxAttempts()) +
                        //TODO improve display of time (see also WaitStrategies)
                        "(" + TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) + "s): ")
                .appendText(failureReason.trim())
                .appendText("\n    Expected: ")
                .appendDescriptionOf(matcher)
                .appendText("\n    Actual values (in order of appearance):");
        int j = 1;
        for (T failingActualValue : suppliedValues) {
            description.appendText("\n         - ");
            description.appendValue(failingActualValue);
            j++;
        }
        throw new RetryAssertionError(description.toString());
    }

    public RetryConfig getConfig() {
        return retryConfig;
    }
}
