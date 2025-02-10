package site.gutschi.humble.spring.common.helper;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TimeHelperTest {

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