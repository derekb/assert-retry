package me.alb_i986.test.asserts.retry.internal;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeoutTest {

    @Test
    public void timeoutShouldBeExpired() throws Exception {
        Timeout oneSecondTimeout = new Timeout(1, TimeUnit.SECONDS);

        Thread.sleep(1000);

        assertTrue(oneSecondTimeout.isExpired());
    }

    @Test
    public void timeoutShouldNotBeExpiredYet() throws Exception {
        Timeout oneSecondTimeout = new Timeout(1, TimeUnit.SECONDS);

        Thread.sleep(20);

        assertFalse(oneSecondTimeout.isExpired());
    }
}