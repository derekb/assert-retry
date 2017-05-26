package me.alb_i986.testing.assertions.retry.internal;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.assertions.retry.RetryConfigBuilder;
import me.alb_i986.testing.assertions.retry.Supplier;
import me.alb_i986.testing.assertions.Suppliers;

import static me.alb_i986.testing.assertions.AssertRetry.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AssertRetryEngineTest {

    // TODO test also logging http://projects.lidalia.org.uk/slf4j-test/

    /**
     * A builder pre-loaded with values which should be ok for most of the tests.
     * Each test may override if needed.
     */
    private final RetryConfigBuilder baseRetryConfig = configureRetry()
            .maxAttempts(2)
            .retryOnException(false)
            .waitStrategy(WaitStrategies.sleep(10, TimeUnit.MILLISECONDS));

    private final Supplier supplierMock = Mockito.mock(Supplier.class);

    @Test
    public void shouldRetryAndEventuallyPassWhenSupplierMatchesWithinMaxRetryTimes() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.maxAttempts(3)
                        .build());

        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        // when
        retry.assertThat(supplierSpy, eventually(is(3)));

        // then
        verify(supplierSpy, times(retry.getConfig().getMaxAttempts())).get();
    }

    @Test
    public void shouldNotRetryWhenSupplierMatchesTheFirstTime() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.maxAttempts(3)
                        .build());

        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        // when
        retry.assertThat(supplierSpy, eventually(is(1)));

        // then
        verify(supplierSpy, times(1)).get();
    }

    @Test
    public void shouldRetryAndEventuallyThrowWhenSupplierDoesntMatchWithinMaxRetryTimes() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.maxAttempts(3)
                        .build());
        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        try {
            retry.assertThat(supplierSpy, eventually(is(4)));
            fail("exception expected");
        } catch (RetryAssertionError expectedException) {
            // exception message should include all of the failing values
            assertThat(expectedException.getMessage(), containsString("Expected: eventually is <4>\n" +
                    "    Actual values (in order of appearance):\n" +
                    "         - <1>\n" +
                    "         - <2>\n" +
                    "         - <3>"));
            verify(supplierSpy, times(retry.getConfig().getMaxAttempts())).get();
        }
    }

    @Test
    public void retryZeroTimes_shouldNotRetryWhenAssertionFailsTheFirstTime() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.maxAttempts(1)
                        .build());
        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        try {
            retry.assertThat(supplierSpy, eventually(is(2)));
            fail("assertion error expected");
        } catch (RetryAssertionError expected) {
            verify(supplierSpy, times(1)).get();
        }
    }

    @Test
    public void retryZeroTimes_whenAssertionPassesTheFirstTime() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.maxAttempts(1)
                        .build());
        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        // when
        retry.assertThat(supplierSpy, eventually(is(1)));

        verify(supplierSpy, times(1)).get();
    }

    @Test
    public void retryOnExceptionIsFalse_shouldNotRetryWhenSupplierThrows() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.retryOnException(false)
                        .build());
        given(supplierMock.get())
                .willThrow(new IllegalArgumentException("supplier failed"));

        try {
            retry.assertThat(supplierMock, equalTo("WHATEVER"));
            fail("exception was expected");
        } catch (RetryAssertionError expectedException) {
            assertThat(expectedException.getCause().getMessage(), is("supplier failed"));
            verify(supplierMock, times(1)).get();
        }
    }

    @Test
    public void retryOnExceptionIsTrue_shouldRetryWhenSupplierThrows() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.retryOnException(true)
                        .build());
        given(supplierMock.get())
                .willThrow(new IllegalArgumentException("supplier failed"));

        try {
            retry.assertThat(supplierMock, equalTo("WHATEVER"));
            fail("exception was expected");
        } catch (RetryAssertionError expected) {
            verify(supplierMock, times(retry.getConfig().getMaxAttempts())).get();
        }
    }

    @Test
    public void retryOnExceptionIsFalse_whenSupplierThrowsTheSecondTime() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.retryOnException(false)
                        .build());
        given(supplierMock.get())
                .willReturn("unexpected string")
                .willThrow(new IllegalArgumentException("supplier failed"));

        try {
            retry.assertThat(supplierMock, equalTo("WHATEVER"));
            fail("exception was expected");
        } catch (RetryAssertionError expectedException) {
            assertThat(expectedException.getMessage(), is("Assertion failed after 2/2 attempts: the supplier of actual values failed"));
            // TODO should also include all of the actual failing values supplied before
            assertThat(expectedException.getCause().getMessage(), is("supplier failed"));
            verify(supplierMock, times(2)).get();
        }
    }
}