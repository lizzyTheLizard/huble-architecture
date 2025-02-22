package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillEntity;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.NextIdEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//TODO: Testing
public class JpaBillRepository implements BillRepository {
    private final ProjectEntityRepository projectEntityRepository;
    private final BillEntityRepository billEntityRepository;
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final NextIdEntityRepository nextIdEntityRepository;

    @Override
    public Set<Bill> findAllForPeriod(LocalDate start) {
        return billEntityRepository.findByBillingPeriodStart(start).stream()
                .map(BillEntity::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Bill> findAllForCostCenter(CostCenter costCenter) {
        final var costCenterEntity = costCenterEntityRepository.getReferenceById(costCenter.getId());
        return billEntityRepository.findByCostCenter(costCenterEntity).stream()
                .map(BillEntity::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Bill bill) {
        final var entity = BillEntity.fromModel(bill, projectEntityRepository);
        billEntityRepository.save(entity);
    }

    @Override
    public int nextId() {
        final var name = "BILL";
        final int nextId = nextIdEntityRepository.findById(name)
                .map(NextIdEntity::getNextId)
                .orElse(1);
        final var newEntity = new NextIdEntity();
        newEntity.setName(name);
        newEntity.setNextId(nextId + 1);
        nextIdEntityRepository.save(newEntity);
        return nextId;
    }
}
