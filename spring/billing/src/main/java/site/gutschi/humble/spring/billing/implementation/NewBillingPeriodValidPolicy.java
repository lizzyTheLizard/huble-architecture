package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.helper.TimeHelper;

@Service
@RequiredArgsConstructor
public class NewBillingPeriodValidPolicy {
    private final BillingPeriodRepository billingPeriodRepository;

    public void ensureFirstOfMonth(BillingPeriod billingPeriod) {
        if (billingPeriod.start().getDayOfMonth() == 1) return;
        throw new InvalidInputException("Billing period must start at the first of the month");
    }

    public void ensureNotInTheFuture(BillingPeriod billingPeriod) {
        final var beginningOfThisMonth = TimeHelper.today().withDayOfMonth(1);
        if (beginningOfThisMonth.isAfter(billingPeriod.start())) return;
        throw new InvalidInputException("Billing period must not be in the future");
    }

    public void ensureNotOverlappingWithExistingPeriods(BillingPeriod billingPeriod) {
        final var latest = billingPeriodRepository.getLatestBillingPeriod();
        if (latest.isEmpty()) return;
        if (billingPeriod.start().isAfter(latest.get().start())) return;
        throw new InvalidInputException("Billing period must not overlap with existing periods");
    }
}
