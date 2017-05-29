package me.alb_i986.testing.assertions.retry.internal;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final String SPACE = " ";

    private static final PeriodFormatter JODA_FORMATTER = new PeriodFormatterBuilder()
            .appendHours()
            .appendSuffix("h")
            .appendSeparator(SPACE)
            .appendMinutes()
            .appendSuffix("m")
            .appendSeparator(SPACE)
            .appendSeconds()
            .appendSuffix("s")
            .appendSeparator(SPACE)
            .appendMillis()
            .appendSuffix("ms")
            .toFormatter();

    protected TimeUtils() {
        // static class
    }

    /**
     * @see #prettyPrint(long)
     */
    public static String prettyPrint(long time, TimeUnit timeUnit) {
        return prettyPrint(timeUnit.toMillis(time));
    }

    /**
     * Pretty prints the given number of milliseconds, up to the hour.
     * <p>
     * Examples:
     * <pre>
     * prettyPrint(60001) -> "1m 1ms"
     * prettyPrint(24 * HOURS_IN_MILLIS) -> "24h" // not "1d"
     * </pre>
     */
    public static String prettyPrint(long millis) {
        return JODA_FORMATTER.print(new Duration(millis).toPeriod());
    }
}
