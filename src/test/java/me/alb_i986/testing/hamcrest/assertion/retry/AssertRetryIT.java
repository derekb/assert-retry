package me.alb_i986.testing.hamcrest.assertion.retry;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.alb_i986.testing.hamcrest.assertion.retry.AssertRetry.*;
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
//                        .timeoutAfter(10, TimeUnit.SECONDS)
        );
    }

}