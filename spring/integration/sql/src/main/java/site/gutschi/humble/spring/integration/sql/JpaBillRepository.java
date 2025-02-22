package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.BillingPeriodEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//TODO BILLING: Test JpaBillRepository
public class JpaBillRepository implements BillRepository {
    private final ProjectEntityRepository projectEntityRepository;
    private final BillEntityRepository billEntityRepository;
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final BillingPeriodEntityRepository billingPeriodEntityRepository;

    @Override
    public Set<Bill> findAllForPeriod(int billingPeriodId) {
        final var billingPeriod = billingPeriodEntityRepository.getReferenceById(billingPeriodId);
        return billEntityRepository.findByBillingPeriod(billingPeriod).stream()
                .map(BillEntity::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Bill> findAllForCostCenter(int costCenterId) {
        final var costCenterEntity = costCenterEntityRepository.getReferenceById(costCenterId);
        return billEntityRepository.findByCostCenter(costCenterEntity).stream()
                .map(BillEntity::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Bill bill) {
        final var entity = BillEntity.fromModel(bill, projectEntityRepository);
        billEntityRepository.save(entity).toModel();
    }
}
