package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillingPeriodEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillingPeriodEntityRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
//TODO: Testing
public class JpaBillingPeriodRepository implements BillingPeriodRepository {
    private final BillingPeriodEntityRepository billingPeriodEntityRepository;

    @Override
    public Optional<BillingPeriod> getLatestBillingPeriod() {
        return billingPeriodEntityRepository.findTopByOrderByBillingPeriodStartDesc()
                .map(BillingPeriodEntity::toModel);
    }

    @Override
    public void save(BillingPeriod billingPeriod) {
        final var entity = BillingPeriodEntity.fromModel(billingPeriod);
        billingPeriodEntityRepository.save(entity);
    }
}
