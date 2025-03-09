package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillingPeriodEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillingPeriodEntityRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaBillingPeriodRepository implements BillingPeriodRepository {
    private final BillingPeriodEntityRepository billingPeriodEntityRepository;

    @Override
    public Optional<BillingPeriod> getLatestBillingPeriod() {
        return billingPeriodEntityRepository.findTopByOrderByStartDesc()
                .map(this::toModel);
    }

    @Override
    public BillingPeriod save(BillingPeriod billingPeriod) {
        final var entity = fromModel(billingPeriod);
        return toModel(billingPeriodEntityRepository.save(entity));
    }

    @Override
    public Set<BillingPeriod> findAll() {
        return billingPeriodEntityRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<BillingPeriod> findById(int id) {
        return billingPeriodEntityRepository.findById(id)
                .map(this::toModel);
    }

    public BillingPeriodEntity fromModel(BillingPeriod billingPeriod) {
        final var entity = new BillingPeriodEntity();
        entity.setId(billingPeriod.id());
        entity.setStart(billingPeriod.start());
        entity.setDueDate(billingPeriod.dueDate());
        entity.setCreatedDate(billingPeriod.createdDate());
        return entity;
    }
    
    public BillingPeriod toModel(BillingPeriodEntity entity) {
        return new BillingPeriod(entity.getId(), entity.getStart(), entity.getDueDate(), entity.getCreatedDate());
    }
}
