package me.alb_i986.testing.assertions.retry.internal;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TimeUtilsPrettyPrintTest {

    private static final long ONE_SECOND_IN_MS = 1000;
    private static final long ONE_MINUTE_IN_MS = 60 * ONE_SECOND_IN_MS;
    private static final long ONE_HOUR_IN_MS = 60 * ONE_MINUTE_IN_MS;
    private static final long ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS;

    @Test
    public void print_0ms() {
        String print = TimeUtils.prettyPrint(0);
        assertThat(print, is("0ms"));
    }

    @Test
    public void print_10ms() {
        String print = TimeUtils.prettyPrint(10);
        assertThat(print, is("10ms"));
    }

    @Test
    public void print_999ms() {
        String print = TimeUtils.prettyPrint(999);
        assertThat(print, is("999ms"));
    }

    @Test
    public void print_1s_1ms() {
        String print = TimeUtils.prettyPrint(ONE_SECOND_IN_MS + 1);
        assertThat(print, is("1s 1ms"));
    }

    @Test
    public void print_1m_1s() {
        String print = TimeUtils.prettyPrint(ONE_MINUTE_IN_MS + ONE_SECOND_IN_MS);
        assertThat(print, is("1m 1s"));
    }

    @Test
    public void print_1h_1s() {
        String print = TimeUtils.prettyPrint(ONE_HOUR_IN_MS + ONE_SECOND_IN_MS);
        assertThat(print, is("1h 1s"));
    }

    @Test
    public void print_1h_1m_1s() {
        String print = TimeUtils.prettyPrint(ONE_HOUR_IN_MS + ONE_MINUTE_IN_MS + ONE_SECOND_IN_MS);
        assertThat(print, is("1h 1m 1s"));
    }

    @Test
    public void print_1day() {
        String print = TimeUtils.prettyPrint(ONE_DAY_IN_MS);
        assertThat(print, is("24h"));
    }

    // let's try other time units as well..

    @Test
    public void print_1m() {
        String print = TimeUtils.prettyPrint(1, TimeUnit.MINUTES);
        assertThat(print, is("1m"));
    }

    @Test
    public void print_1h() {
        String print = TimeUtils.prettyPrint(1, TimeUnit.HOURS);
        assertThat(print, is("1h"));
    }

    /**
     * Doesn't make much sense to display milliseconds
     * when the total involves minutes, or (worse) hours.
     */
    @Test
    @Ignore // TODO
    public void shouldRoundMillisecondsWhenDurationIsOverOneMinute() {
        assertThat(TimeUtils.prettyPrint(ONE_MINUTE_IN_MS + 999),
                is("1m 1s"));

        assertThat(TimeUtils.prettyPrint(ONE_MINUTE_IN_MS + ONE_SECOND_IN_MS + 1, TimeUnit.MILLISECONDS),
                is("1m 1s"));

        assertThat(TimeUtils.prettyPrint(ONE_HOUR_IN_MS + 1, TimeUnit.MILLISECONDS),
                is("1h"));

        assertThat(TimeUtils.prettyPrint(ONE_HOUR_IN_MS + ONE_SECOND_IN_MS + 1, TimeUnit.MILLISECONDS),
                is("1h 1s"));
    }
}