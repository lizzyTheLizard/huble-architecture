package site.gutschi.humble.spring.common.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Helper class to get the current time. Use this class instead of calling {@link Instant#now()} directly to make testing easier.
 */
public class TimeHelper {
    private static Instant now = null;

    private TimeHelper() {
    }

    public static Instant now() {
        if (now != null) {
            return now;
        }
        return Instant.now();
    }

    public static void setNow(Instant now) {
        TimeHelper.now = now;
    }

    public static LocalDate today() {
        return dateOf(now());
    }

    public static LocalDate dateOf(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Instant instantOf(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

}
