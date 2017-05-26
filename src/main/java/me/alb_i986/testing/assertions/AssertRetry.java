package me.alb_i986.testing.assertions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.assertions.retry.internal.AssertRetryEngine;
import me.alb_i986.testing.assertions.retry.internal.RetryAssertionError;
import me.alb_i986.testing.assertions.retry.internal.RetryConfig;
import me.alb_i986.testing.assertions.retry.RetryConfigBuilder;
import me.alb_i986.testing.assertions.retry.Supplier;
import me.alb_i986.testing.assertions.retry.WaitStrategies;

/**
 * Assertion methods allowing for making assertions <i>with tolerance</i>,
 * i.e. assertions which may fail a few times but <i>eventually</i> pass.
 * Typically useful when testing async systems like message queues.
 * <p>
 * It is recommended to star-statically import this class:
 *
 * <pre>
 * import static me.alb_i986.testing.assertions.AssertRetry.*;
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
     * implicitly using an empty {@code failureExplanation} and the {@link RetryConfigBuilder.DefaultValues}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfigBuilder)
     * @see RetryConfigBuilder.DefaultValues
     */
    public static <T> T assertThat(Supplier<T> actualValuesSupplier, Matcher<? super T> matcher) {
        return assertThat("", actualValuesSupplier, matcher);
    }

    /**
     * Handy overloaded version of the retry assertion method,
     * implicitly configured with {@link RetryConfigBuilder.DefaultValues}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfigBuilder)
     * @see RetryConfigBuilder.DefaultValues
     */
    public static <T> T assertThat(String failureExplanation, Supplier<T> actualValuesSupplier, Matcher<? super T> matcher) {
        return assertThat(failureExplanation, actualValuesSupplier, matcher, configureRetry());
    }

    /**
     * Handy overloaded version of the retry assertion method,
     * implicitly using an empty {@code failureExplanation}.
     *
     * @see #assertThat(String, Supplier, Matcher, RetryConfigBuilder)
     */
    public static <T> T assertThat(Supplier<T> actualValuesSupplier, Matcher<? super T> matcher, RetryConfigBuilder retryConfig) {
        return assertThat("", actualValuesSupplier, matcher, retryConfig);
    }

    /**
     * Assertion method along the lines of JUnit/Hamcrest's {@link org.hamcrest.MatcherAssert#assertThat(String, Object, Matcher)},
     * featuring a retry mechanism to support assertions <i>with tolerance</i>.
     * <p>
     * Allows to verify that the actual value returned by the {@code supplier} <i>eventually</i>
     * satisfies the condition specified by the {@code matcher}, within the configured number of attempts.
     * If not, an AssertionError is thrown with information about the expected value and
     * <i>all of</i> the actual failing values.
     * <p>
     * Basically, it runs the assertion {@code org.junit.Assert.assertThat(supplier.get(), matcher)}
     * until it passes, or for max {@code n} times.
     * <p>
     * Let's see a concrete example.
     * Say that we have a JMS queue, and we need to verify that a message with body "expected content"
     * is published on the queue.
     * Given the async nature of the system, we need to employ a bit of tolerance in our assertions.
     *
     * <pre>
     * import static me.alb_i986.testing.assertions.AssertRetry.*;
     *
     * MessageConsumer consumer = session.createConsumer(queue);
     * connection.start();
     * Supplier&lt;String&gt; messageText = new Supplier<>() {
     *   {@literal @}Override
     *    public String get() throws JMSException {
     *       TextMessage m = (TextMessage) consumer.receiveNoWait();  // polling for messages, non blocking
     *       return m == null ? null : m.getText();
     *    }
     * };
     * assertThat(messageText, eventually(containsString("expected content")),
     *         configureRetry()
     *             .maxAttempts(10)
     *             .waitStrategy(WaitStrategies.sleep(5, TimeUnit.SECONDS));
     *             .retryOnException(true));
     * </pre>
     *
     * The first few lines set up the supplier of actual values, which will be used to poll the message queue
     * for messages.
     * BTW, it is recommended to extract the Supplier variable to a method, in order to help with code reuse.
     * <p>
     * Then we have our assertion method.
     * It reads very much like a JUnit/Hamcrest {@code assertThat} assertion.
     * In this case it's asserting that the expected text message will be received within 10 attempts.
     * After each failing attempt, it will wait for 5s, and then try again.
     * If {@code consumer.receiveNoWait()} throws a JMSException, the assertion will be re-tried,
     * as if it returned a non-matching value.
     * Finally, the assertion will timeout after 45s ({@code (10 - 1) * 5s})
     * (excluding the time it takes for the supplier to get the actual value),
     * and an AssertionError similar to the following will be thrown:
     * <pre>
     * java.lang.AssertionError: Assertion failed after 10/10 attempts (49s):
     *     Expected: eventually a string containing "expected content"
     *     Actual values: (in order of appearance)
     *       - "some content"
     *       - null
     *       - "some other content"
     * </pre>
     * Please note the use of the matcher {@link AssertRetry#eventually(Matcher)}.
     * It's just syntactic sugar which makes the assertion read better: helps the reader see
     * that the assertion employs a retry mechanism.
     * Especially useful with the overloaded versions of this method which do not take a {@link RetryConfig}.
     *
     * <h3>Configuration</h3>
     * The retry mechanism can be configured in terms of:
     * <ul>
     *     <li>how many times to retry the assertion for: {@link RetryConfigBuilder#maxAttempts(int)}</li>
     *     <li>the wait strategy: {@link RetryConfigBuilder#waitStrategy(Runnable)}
     *     (e.g. {@link WaitStrategies#sleep(long, TimeUnit)})</li>
     *     <li>whether to retry in case the {@code supplier} throws: {@link RetryConfigBuilder#retryOnException(boolean)}</li>
     * </ul>
     *
     * As shown in the example above, {@link #configureRetry()} provides access to the builder API of the retry configuration.
     *
     * @param <T> the type of the actual values
     *
     * @param failureExplanation this string will be included in the exception message in case the assertion fails all the times
     *
     * @param actualValuesSupplier a {@link Runnable} providing the code to get the actual value as many times as needed.
     *                             {@link Supplier#get()} is supposed to re-compute the value from scratch each time.
     * @param matcher an Hamcrest matcher, encapsulating the condition under which the actual value is as expected
     * @param retryConfig the configuration of the retry mechanism
     * @return the first actual value returned by the supplier which satisfies the matcher
     *
     * @throws AssertionError if the assertion fails all the times
     *
     * @see #configureRetry()
     * @see #eventually(Matcher)
     */
    public static <T> T assertThat(String failureExplanation, Supplier<T> actualValuesSupplier,
                                   Matcher<? super T> matcher, RetryConfigBuilder retryConfig) {
        try {
            return new AssertRetryEngine(retryConfig.build())
                    .assertThat(failureExplanation, actualValuesSupplier, matcher);
        } catch (RetryAssertionError e) { // re-throw as a plain AssertionError
            throw new AssertionError(e.getMessage(), e.getCause());
        }
    }

    /**
     * Syntactic sugar which makes
     * {@link AssertRetry#assertThat(String, Supplier, Matcher, RetryConfigBuilder) retry assertions}
     * read better. Example:
     * <pre>
     * assertThat(supplier, eventually(is(greaterThan(2))));
     * </pre>
     */
    public static <T> Matcher<T> eventually(final Matcher<T> matcher) {
        return new Is<T>(matcher) {
            @Override
            public void describeTo(Description description) {
                description.appendText("eventually ").appendDescriptionOf(matcher);
            }
        };
    }

    /**
     * Provides access to a fluent DSL for configuring the retry mechanism.
     * <p>
     * If any one of parameters has not been explicitly set,
     * the {@link me.alb_i986.testing.assertions.retry.RetryConfigBuilder.DefaultValues default value}
     * will be used.
     *
     * @see RetryConfigBuilder
     * @see me.alb_i986.testing.assertions.retry.RetryConfigBuilder.DefaultValues
     */
    @SuppressWarnings("deprecation")
    public static RetryConfigBuilder configureRetry() {
        return new RetryConfigBuilder();
    }
}
