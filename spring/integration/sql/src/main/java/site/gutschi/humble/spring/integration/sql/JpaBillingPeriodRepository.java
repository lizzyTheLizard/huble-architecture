package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillingPeriodEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillingPeriodEntityRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JpaBillingPeriodRepository implements BillingPeriodRepository {
    private final BillingPeriodEntityRepository billingPeriodEntityRepository;

    @Override
    public Optional<BillingPeriod> getLatestBillingPeriod() {
        return billingPeriodEntityRepository.findTopByOrderByStartDesc()
                .map(BillingPeriodEntity::toModel);
    }

    @Override
    public BillingPeriod save(BillingPeriod billingPeriod) {
        final var entity = BillingPeriodEntity.fromModel(billingPeriod);
        return billingPeriodEntityRepository.save(entity).toModel();
    }

    @Override
    public Set<BillingPeriod> findAll() {
        return billingPeriodEntityRepository.findAll().stream()
                .map(BillingPeriodEntity::toModel)
                .collect(java.util.stream.Collectors.toSet());
    }
}
