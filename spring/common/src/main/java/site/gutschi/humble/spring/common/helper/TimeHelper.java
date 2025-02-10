package site.gutschi.humble.spring.common.helper;

import java.time.Instant;

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
}
