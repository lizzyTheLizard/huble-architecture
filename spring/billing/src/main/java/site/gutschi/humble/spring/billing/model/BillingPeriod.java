package site.gutschi.humble.spring.billing.model;

import java.time.LocalDate;

/**
 * A billing period for which a bill is created. A billing period is created once by a system admin and cannot be changed.
 *
 * @param id          The ID of the billing period. Is null before the billing period is persisted.
 * @param start       The start date of the billing period.
 * @param dueDate     The due date of the billing period.
 * @param createdDate The date when the billing period was created.
 */
//TODO BILLING: Unit-Test BillingPeriod
public record BillingPeriod(
        Integer id,
        LocalDate start,
        LocalDate dueDate,
        LocalDate createdDate) {

    public boolean isIn(LocalDate date) {
        return isInOrBefore(date) && isInOrAfter(date);
    }

    public boolean isInOrBefore(LocalDate date) {
        final var nextStart = start.plusMonths(1);
        return date.isBefore(nextStart);
    }

    public boolean isInOrAfter(LocalDate date) {
        return !date.isBefore(start);
    }
}
