package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "bill")
public class BillEntity {
    @Id
    private int id;
    @ManyToOne
    @NotNull
    private CostCenterEntity costCenter;
    @NotNull
    private LocalDate billingPeriodStart;
    @NotNull
    private LocalDate dueDate;
    @NotNull
    private LocalDate createdDate;
    private Set<ProjectBillEntity> projectBills;

    public static BillEntity fromModel(Bill bill, ProjectEntityRepository projectEntityRepository) {
        final var entity = new BillEntity();
        entity.setId(bill.getId());
        entity.setCostCenter(CostCenterEntity.fromModel(bill.getCostCenter()));
        entity.setBillingPeriodStart(bill.getBillingPeriodStart());
        entity.setDueDate(bill.getDueDate());
        entity.setCreatedDate(bill.getCreatedDate());
        entity.setProjectBills(bill.getProjectBills().stream()
                .map((ProjectBill pb) -> ProjectBillEntity.fromModel(pb, projectEntityRepository))
                .collect(Collectors.toSet())
        );
        return entity;
    }

    public Bill toModel() {
        final var projectBills = this.projectBills.stream()
                .map(ProjectBillEntity::toModel)
                .collect(Collectors.toSet());
        final var costCenter = this.costCenter.toModel();
        return Bill.builder()
                .id(id)
                .billingPeriodStart(billingPeriodStart)
                .createdDate(createdDate)
                .dueDate(dueDate)
                .projectBills(projectBills)
                .costCenter(costCenter)
                .build();
    }
}
