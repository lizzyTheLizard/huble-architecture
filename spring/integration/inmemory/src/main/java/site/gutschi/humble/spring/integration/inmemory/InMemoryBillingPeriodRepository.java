package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class InMemoryBillingPeriodRepository implements BillingPeriodRepository {
    private final Set<BillingPeriod> billingPeriods = new HashSet<>();

    @Override
    public Optional<BillingPeriod> getLatestBillingPeriod() {
        return billingPeriods.stream()
                .max((a, b) -> b.getBillingPeriodStart().compareTo(a.getBillingPeriodStart()));
    }

    @Override
    public void save(BillingPeriod newBillingPeriod) {
        billingPeriods.removeIf(billingPeriod -> billingPeriod.getBillingPeriodStart().equals(newBillingPeriod.getBillingPeriodStart()));
        billingPeriods.add(newBillingPeriod);
    }
}
