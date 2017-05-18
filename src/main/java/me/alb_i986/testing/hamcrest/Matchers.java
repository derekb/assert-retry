package me.alb_i986.testing.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

import me.alb_i986.testing.hamcrest.assertion.RetryAssert;
import me.alb_i986.testing.hamcrest.assertion.retry.RetryConfig;
import me.alb_i986.testing.hamcrest.assertion.retry.Supplier;

/**
 * Custom factory of {@link Matcher}s.
 * <p>
 * By importing this class, all of the official matchers in hamcrest's {@link org.hamcrest.Matchers}
 * are automatically imported as well.
 */
public class Matchers extends org.hamcrest.Matchers {

    private Matchers() {
        // static class
    }

    /**
     * Syntactic sugar which makes
     * {@link RetryAssert#assertThat(String, Supplier, Matcher, RetryConfig) retry assertions}
     * read better. Example:
     * <pre>
     * assertThat(supplier, eventually(is(greaterThan(2))));
     * </pre>
     *
     * @see RetryAssert#assertThat(String, Supplier, Matcher, RetryConfig)
     */
    public static <T> Matcher<T> eventually(final Matcher<T> matcher) {
        return new Is<T>(matcher) {
            @Override
            public void describeTo(Description description) {
                description.appendText("eventually ").appendDescriptionOf(matcher);
            }
        };
    }
}
