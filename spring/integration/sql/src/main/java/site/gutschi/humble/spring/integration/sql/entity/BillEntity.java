package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

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
    @ManyToOne
    private BillingPeriodEntity billingPeriod;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectBillEntity> projectBills;

    public static BillEntity fromModel(Bill bill, ProjectEntityRepository projectEntityRepository) {
        final var entity = new BillEntity();
        entity.setId(bill.id());
        entity.setCostCenter(CostCenterEntity.fromModel(bill.costCenter()));
        entity.setBillingPeriod(BillingPeriodEntity.fromModel(bill.billingPeriod()));
        entity.setProjectBills(bill.projectBills().stream()
                .map((ProjectBill pb) -> ProjectBillEntity.fromModel(pb, projectEntityRepository))
                .collect(Collectors.toSet())
        );
        return entity;
    }

    public Bill toModel() {
        final var projectBills = this.projectBills.stream()
                .map(ProjectBillEntity::toModel)
                .collect(Collectors.toSet());
        return new Bill(id, costCenter.toModel(), billingPeriod.toModel(), projectBills);
    }
}
