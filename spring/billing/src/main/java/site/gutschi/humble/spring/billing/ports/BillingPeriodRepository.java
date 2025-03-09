package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.BillingPeriod;

import java.util.Collection;
import java.util.Optional;

public interface BillingPeriodRepository {
    /**
     * Returns the newest billing period. Returns empty if no billing period exists.
     */
    Optional<BillingPeriod> getLatestBillingPeriod();

    /**
     * Saves a new billing period and returns the saved billing period.
     */
    BillingPeriod save(BillingPeriod newBillingPeriod);

    /**
     * Returns all billing periods
     */
    Collection<BillingPeriod> findAll();

    /**
     * Returns the billing period with the given ID. Returns empty if no billing period exists.
     */
    Optional<BillingPeriod> findById(int billingPeriodId);
}
