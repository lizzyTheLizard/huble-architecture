package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.BillingPeriod;

import java.util.Optional;

//TODO: Document
public interface BillingPeriodRepository {
    Optional<BillingPeriod> getLatestBillingPeriod();

    void save(BillingPeriod newBillingPeriod);
}
