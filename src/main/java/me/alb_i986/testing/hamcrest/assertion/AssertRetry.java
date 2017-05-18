package me.alb_i986.testing.hamcrest.assertion;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.hamcrest.assertion.retry.AssertRetryEngine;
import me.alb_i986.testing.hamcrest.assertion.retry.RetryAssertionError;
import me.alb_i986.testing.hamcrest.assertion.retry.RetryConfig;
import me.alb_i986.testing.hamcrest.assertion.retry.Supplier;
import me.alb_i986.testing.hamcrest.assertion.retry.WaitStrategies;

/**
 * Static class defining assertion methods allowing for making assertions <i>with tolerance</i>,
 * i.e. assertions which may fail a few times but <i>eventually</i> pass.
 * <p>
 * It is recommended to star-statically import this class:
 *
 * <pre>
 * import me.alb_i986.testing.hamcrest.assertion.AssertRetry.*;
 * </pre>
 *
 * @author Alberto Scotto
 */
public class AssertRetry {

    protected AssertRetry() {
        // static class
    }

    /**
     * Handy overloaded version of the retry assertion method,
     * implicitly using an empty {@code failureExplanation} and the {@link RetryConfig#DEFAULT_CONFIG}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfig)
     * @see RetryConfig#DEFAULT_CONFIG
     */
    public static <T> T assertThat(Supplier<T> actualValuesSupplier, Matcher<? super T> matcher) {
        return assertThat("", actualValuesSupplier, matcher);
    }

    /**
     * Handy overloaded version of the retry assertion method,
     * implicitly configured with {@link RetryConfig#DEFAULT_CONFIG}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfig)
     * @see RetryConfig#DEFAULT_CONFIG
     */
    public static <T> T assertThat(String failureExplanation, Supplier<T> actualValuesSupplier, Matcher<? super T> matcher) {
        return assertThat(failureExplanation, actualValuesSupplier, matcher, RetryConfig.DEFAULT_CONFIG);
    }

    /**
     * Handy overloaded version of the retry assertion method,
     * implicitly using an empty {@code failureExplanation}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfig)
     */
    public static <T> T assertThat(Supplier<T> actualValuesSupplier, Matcher<? super T> matcher, RetryConfig retryConfig) {
        return assertThat("", actualValuesSupplier, matcher, retryConfig);
    }

    /**
     * Assertion method featuring a retry mechanism, verifying that the actual value returned by the
     * {@code supplier} <i>eventually</i> satisfies the condition specified by the {@code matcher}.
     * If not, an {@link AssertionError} is thrown with information about the expected value and
     * <i>all of</i> the actual failing values.
     * <p>The assertion is retried for max {@code n} times, where {@code n} is given by
     * {@link RetryConfig#getMaxAttempts()}.
     *
     * <p>Example:
     * <pre>
     * import me.alb_i986.testing.hamcrest.assertion.AssertRetry.*;
     *
     * MessageConsumer consumer = session.createConsumer(queue);
     * connection.start();
     * Supplier&lt;TextMessage&gt; message = new Supplier<>() {
     *   {@literal @}Override
     *    public TextMessage get() throws JMSException {
     *       return (TextMessage) consumer.receiveNoWait(); // polling for messages, without blocking
     *    }
     * };
     * AssertRetry.assertThat(message.getText(), eventually(containsString("expected content")),
     *         RetryConfig.builder()
     *             .withMaxAttempts(10)
     *             .withWaitStrategy(WaitStrategies.sleep(5, TimeUnit.SECONDS));
     *             .withRetryOnException(true)
     *             .build());
     * </pre>
     * In this example we are asserting that a message with body "expected content" is eventually
     * published on a JMS queue, within 10 times.
     *
     * or {@link AssertionError}
     *
     * <h3>Waits</h3>
     * Between one attempt and the other, the system waits as configured in
     * {@link RetryConfig#getWaitStrategy()}, e.g. {@link WaitStrategies#sleep(long, TimeUnit)}.
     * <p>More advanced usages may wait for a specific event to occur,
     * e.g. for a message to be published on a queue.
     *
     * <h3>Retry on exception</h3>
     * If {@link RetryConfig#isRetryOnException()} has been configured to {@code true}
     * and {@link Supplier#get()} throws an {@link Exception} or an {@link AssertionError},
     * the assertion is re-tried as if the supplier had returned a value which did not satisfy the matcher.
     *
     * <h3>Configuration</h3>
     * The retry mechanism can be configured by supplying an instance of {@link RetryConfig},
     * which can be built by using {@link RetryConfig#builder()}.
     *
     * @param actualValuesSupplier a closure providing the code to get the actual value as many times as needed.
     *                             {@link Supplier#get()} is assumed to re-compute the value from scratch.
     * @param matcher encapsulates the condition under which the actual value is as expected
     * @param retryConfig collects all the parameters configuring the retry mechanism
     * @param failureExplanation this string will be included in the exception message in case the assertion fails all the times
     *
     * @param <T> the type of the actual value
     *
     * @return the first actual value returned by the supplier which satisfies the matcher
     *
     * @throws AssertionError if the assertion fails all the times, i.e. {@link RetryConfig#getMaxAttempts()}
     */
    public static <T> T assertThat(String failureExplanation, Supplier<T> actualValuesSupplier,
                                   Matcher<? super T> matcher, RetryConfig retryConfig) {
        try {
            return new AssertRetryEngine(retryConfig)
                    .assertThat(failureExplanation, actualValuesSupplier, matcher);
        } catch (RetryAssertionError e) { // re-throw as a plain AssertionError
            throw new AssertionError(e.getMessage(), e.getCause());
        }
    }
}
