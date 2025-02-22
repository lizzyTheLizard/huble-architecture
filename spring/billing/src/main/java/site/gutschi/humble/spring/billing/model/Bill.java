package site.gutschi.humble.spring.billing.model;

import java.util.Set;

/**
 * A single bill for a cost center for a billing period.
 * A bill is created once by a system admin and cannot be changed.
 *
 * @param id            The unique identifier of the bill. Is null before the bill is persisted.
 * @param costCenter    The cost center for which the bill is created.
 * @param billingPeriod The billing period for which the bill is created.
 * @param projectBills  The project bills that are part of this bill.
 */
public record Bill(
        Integer id,
        CostCenter costCenter,
        BillingPeriod billingPeriod,
        Set<ProjectBill> projectBills) {
}
