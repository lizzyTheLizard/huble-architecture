package site.gutschi.humble.spring.billing.model;

import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.time.LocalDate;

/**
 * A billing period for which a bill is created. A billing period is created once by a system admin and cannot be changed.
 *
 * @param id          The ID of the billing period. Is null before the billing period is persisted.
 * @param start       The start date of the billing period.
 * @param dueDate     The due date of the billing period.
 * @param createdDate The date when the billing period was created.
 */
public record BillingPeriod(
        Integer id,
        LocalDate start,
        LocalDate dueDate,
        LocalDate createdDate) {

    public static BillingPeriod createNew(LocalDate start) {
        final var today = TimeHelper.today();
        final var dueDate = today.plusDays(30);
        return new BillingPeriod(null, start, dueDate, today);
    }

    public boolean isIn(LocalDate date) {
        return isInOrBefore(date) && isInOrAfter(date);
    }

    public boolean isInOrBefore(LocalDate date) {
        final var nextStart = start.plusMonths(1);
        return date.isBefore(nextStart);
    }

    public boolean isBefore(LocalDate date) {
        return date.isBefore(start);
    }

    public boolean isInOrAfter(LocalDate date) {
        return !date.isBefore(start);
    }
}
