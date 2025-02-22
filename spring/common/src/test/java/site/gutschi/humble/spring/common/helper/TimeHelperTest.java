package site.gutschi.humble.spring.common.helper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeHelperTest {

    @Nested
    class Now {
        @Test
        void noFixedTimeSet() {
            final var result = TimeHelper.now();

            assertThat(result).isBetween(Instant.now().minusSeconds(10), Instant.now().plusSeconds(10));
        }

        @Test
        void fixedTimeSet() {
            final var dummyTime = Instant.ofEpochMilli(1000);
            TimeHelper.setNow(dummyTime);

            final var result = TimeHelper.now();

            assertThat(result).isEqualTo(dummyTime);
        }

        @Test
        void fixedTimeUnSet() {
            final var dummyTime = Instant.ofEpochMilli(1000);
            TimeHelper.setNow(dummyTime);
            TimeHelper.setNow(null);

            final var result = TimeHelper.now();

            assertThat(result).isBetween(Instant.now().minusSeconds(10), Instant.now().plusSeconds(10));
        }

    }

    @Nested
    class Today {
        @Test
        void noFixedTimeSet() {
            final var result = TimeHelper.today();

            assertThat(result).isEqualTo(LocalDate.now());
        }

        @Test
        void fixedTimeSet() {
            final var dummyTime = Instant.ofEpochMilli(1000);
            TimeHelper.setNow(dummyTime);

            final var result = TimeHelper.today();

            assertThat(result).isEqualTo(LocalDate.of(1970, 1, 1));
        }

        @Test
        void fixedTimeUnSet() {
            final var dummyTime = Instant.ofEpochMilli(1000);
            TimeHelper.setNow(dummyTime);
            TimeHelper.setNow(null);

            final var result = TimeHelper.today();

            assertThat(result).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    class DateOf {
        @Test
        void dateOf() {
            final var dummyDate = LocalDate.of(1915, 3, 5);
            final var instant = dummyDate.atTime(12, 0).toInstant(ZoneOffset.UTC);

            final var result = TimeHelper.dateOf(instant);

            assertThat(result).isEqualTo(dummyDate);
        }
    }
}