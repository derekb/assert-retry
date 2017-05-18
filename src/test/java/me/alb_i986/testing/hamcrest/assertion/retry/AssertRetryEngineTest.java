package me.alb_i986.testing.hamcrest.assertion.retry;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import static me.alb_i986.testing.hamcrest.Matchers.containsString;
import static me.alb_i986.testing.hamcrest.Matchers.equalTo;
import static me.alb_i986.testing.hamcrest.Matchers.eventually;
import static me.alb_i986.testing.hamcrest.Matchers.is;
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
    private final RetryConfig.Builder baseRetryConfig = RetryConfig.builder()
            .withMaxAttempts(2)
            .withRetryOnException(false)
            .withWaitStrategy(WaitStrategies.sleep(10, TimeUnit.MILLISECONDS));

    private final Supplier supplierMock = Mockito.mock(Supplier.class);

    @Test
    public void shouldRetryAndEventuallyPassWhenSupplierMatchesWithinMaxRetryTimes() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.withMaxAttempts(3)
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
                baseRetryConfig.withMaxAttempts(3)
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
                baseRetryConfig.withMaxAttempts(3)
                        .build());
        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        try {
            retry.assertThat(supplierSpy, eventually(is(4)));
            fail("exception expected");
        } catch (RetryAssertionError expectedException) {
            // exception message should include all of the failing values
            assertThat(expectedException.getMessage(), containsString("Expected: eventually is <4>\n" +
                    "     but:\n" +
                    "         1. was <1>\n" +
                    "         2. was <2>\n" +
                    "         3. was <3>"));
            verify(supplierSpy, times(retry.getConfig().getMaxAttempts())).get();
        }
    }

    @Test
    public void retryZeroTimes_shouldNotRetryWhenAssertionFailsTheFirstTime() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.withMaxAttempts(1)
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
                baseRetryConfig.withMaxAttempts(1)
                        .build());
        Supplier<Integer> supplierSpy = Mockito.spy(Suppliers.ascendingIntegersStartingFrom(1));

        // when
        retry.assertThat(supplierSpy, eventually(is(1)));

        verify(supplierSpy, times(1)).get();
    }

    @Test
    public void retryOnExceptionIsFalse_shouldNotRetryWhenSupplierThrows() throws Exception {
        AssertRetryEngine retry = new AssertRetryEngine(
                baseRetryConfig.withRetryOnException(false)
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
                baseRetryConfig.withRetryOnException(true)
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
}