package me.alb_i986.testing.assertions;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.alb_i986.testing.assertions.retry.Supplier;
import me.alb_i986.testing.assertions.retry.WaitStrategies;

import static me.alb_i986.testing.assertions.AssertRetry.*;
import static org.hamcrest.Matchers.*;

public class AssertRetryIT {

    @Test
    public void supplierEventuallyReturnsMatchingValue() {
        final Supplier<String> actual = new Supplier<String>() {
            int i = 0;
            List<String> actuals = Arrays.asList("a", "b", "c");

            @Override
            public String get() throws Exception {
                return actuals.get(i++);
            }
        };
        assertThat(actual, eventually(containsString("c")),
                configureRetry()
                        .retryOnException(false)
                        .maxAttempts(3)
                        .waitStrategy(WaitStrategies.sleep(2, TimeUnit.SECONDS))
                        // TODO
//                        .sleepBetweenAttempts(5, TimeUnit.SECONDS)
//                        .waitBetweenAttempts(WaitStrategies.sleep(5, TimeUnit.SECONDS))
        );
    }

    @Test(expected = AssertionError.class)
    public void retryWithTimeout_supplierNeverMatches() {
        final Supplier<String> actual = new Supplier<String>() {
            @Override
            public String get() throws Exception {
                return "a";
            }
        };
        assertThat(actual, eventually(containsString("c")),
                configureRetry()
                        .timeoutAfter(1, TimeUnit.SECONDS)
                        .waitStrategy(WaitStrategies.sleep(100, TimeUnit.MILLISECONDS))
                        .retryOnException(false)
                        .maxAttempts(Integer.MAX_VALUE)
        );
    }

}