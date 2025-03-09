package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.integration.sql.entity.BillEntity;
import site.gutschi.humble.spring.integration.sql.entity.ProjectBillEntity;
import site.gutschi.humble.spring.integration.sql.repo.BillEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.BillingPeriodEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaBillRepository implements BillRepository {
    private final ProjectEntityRepository projectEntityRepository;
    private final BillEntityRepository billEntityRepository;
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final BillingPeriodEntityRepository billingPeriodEntityRepository;
    private final JpaBillingPeriodRepository jpaBillingPeriodRepository;
    private final JpaCostCenterRepository jpaCostCenterRepository;
    private final JpaProjectRepository jpaProjectRepository;

    @Override
    public Set<Bill> findAllForPeriod(BillingPeriod billingPeriod) {
        final var billingPeriodEntity = jpaBillingPeriodRepository.fromModel(billingPeriod);
        return billEntityRepository.findByBillingPeriod(billingPeriodEntity).stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Bill> findAllForCostCenter(CostCenter costCenter) {
        final var costCenterEntity = costCenterEntityRepository.getReferenceById(costCenter.getId());
        return billEntityRepository.findByCostCenter(costCenterEntity).stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Bill bill) {
        final var entity = fromModel(bill);
        billEntityRepository.save(entity);
    }

    private BillEntity fromModel(Bill bill) {
        final var entity = new BillEntity();
        entity.setId(bill.id());
        entity.setCostCenter(costCenterEntityRepository.getReferenceById(bill.costCenter().getId()));
        entity.setBillingPeriod(billingPeriodEntityRepository.getReferenceById(bill.billingPeriod().id()));
        entity.setProjectBills(bill.projectBills().stream()
                .map((ProjectBill pb) -> fromModel(pb, entity))
                .collect(Collectors.toSet())
        );
        return entity;
    }

    private Bill toModel(BillEntity entity) {
        final var projectBills = entity.getProjectBills().stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
        return new Bill(entity.getId(), jpaCostCenterRepository.toModel(entity.getCostCenter()), jpaBillingPeriodRepository.toModel(entity.getBillingPeriod()), projectBills);
    }

    private ProjectBillEntity fromModel(ProjectBill pb, BillEntity bill) {
        final var entity = new ProjectBillEntity();
        entity.setProject(projectEntityRepository.getReferenceById(pb.project().getKey()));
        entity.setAmount(pb.amount());
        entity.setTotalNonDeletedTasks(pb.totalNonDeletedTasks());
        entity.setCreatedTasks(pb.createdTasks());
        entity.setBill(bill);
        return entity;
    }

    public ProjectBill toModel(ProjectBillEntity entity) {
        final var project = jpaProjectRepository.toModel(entity.getProject());
        return new ProjectBill(project, entity.getAmount(), entity.getTotalNonDeletedTasks(), entity.getCreatedTasks());
    }
}
