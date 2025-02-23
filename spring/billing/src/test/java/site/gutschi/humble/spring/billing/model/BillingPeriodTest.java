package site.gutschi.humble.spring.billing.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BillingPeriodTest {

    @Test
    void isIn() {
        final var target = new BillingPeriod(1, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31), LocalDate.of(2021, 1, 1));

        assertThat(target.isIn(LocalDate.of(2020, 12, 31))).isFalse();
        assertThat(target.isIn(LocalDate.of(2021, 1, 1))).isTrue();
        assertThat(target.isIn(LocalDate.of(2021, 1, 3))).isTrue();
        assertThat(target.isIn(LocalDate.of(2021, 1, 31))).isTrue();
        assertThat(target.isIn(LocalDate.of(2021, 2, 1))).isFalse();
    }

    @Test
    void isInOrAfter() {
        final var target = new BillingPeriod(1, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31), LocalDate.of(2021, 1, 1));

        assertThat(target.isInOrAfter(LocalDate.of(2020, 12, 31))).isFalse();
        assertThat(target.isInOrAfter(LocalDate.of(2021, 1, 1))).isTrue();
        assertThat(target.isInOrAfter(LocalDate.of(2021, 1, 3))).isTrue();
        assertThat(target.isInOrAfter(LocalDate.of(2021, 1, 31))).isTrue();
        assertThat(target.isInOrAfter(LocalDate.of(2021, 2, 1))).isTrue();
    }

    @Test
    void isInOrBefore() {
        final var target = new BillingPeriod(1, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31), LocalDate.of(2021, 1, 1));

        assertThat(target.isInOrBefore(LocalDate.of(2020, 12, 31))).isTrue();
        assertThat(target.isInOrBefore(LocalDate.of(2021, 1, 1))).isTrue();
        assertThat(target.isInOrBefore(LocalDate.of(2021, 1, 3))).isTrue();
        assertThat(target.isInOrBefore(LocalDate.of(2021, 1, 31))).isTrue();
        assertThat(target.isInOrBefore(LocalDate.of(2021, 2, 1))).isFalse();
    }

    @Test
    void isBefore() {
        final var target = new BillingPeriod(1, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31), LocalDate.of(2021, 1, 1));

        assertThat(target.isBefore(LocalDate.of(2020, 12, 31))).isTrue();
        assertThat(target.isBefore(LocalDate.of(2021, 1, 1))).isFalse();
        assertThat(target.isBefore(LocalDate.of(2021, 1, 3))).isFalse();
        assertThat(target.isBefore(LocalDate.of(2021, 1, 31))).isFalse();
        assertThat(target.isBefore(LocalDate.of(2021, 2, 1))).isFalse();
    }

}