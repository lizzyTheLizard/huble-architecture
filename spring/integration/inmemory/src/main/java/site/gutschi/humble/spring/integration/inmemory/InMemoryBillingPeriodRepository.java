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
                .max((a, b) -> b.start().compareTo(a.start()));
    }

    @Override
    public BillingPeriod save(BillingPeriod newBillingPeriod) {
        if (newBillingPeriod.id() != null)
            billingPeriods.removeIf(billingPeriod -> billingPeriod.id().equals(newBillingPeriod.id()));
        final var result = new BillingPeriod(
                newBillingPeriod.id() != null ? newBillingPeriod.id() : billingPeriods.size() + 1,
                newBillingPeriod.start(),
                newBillingPeriod.dueDate(),
                newBillingPeriod.createdDate()
        );
        billingPeriods.add(result);
        return result;
    }
}
