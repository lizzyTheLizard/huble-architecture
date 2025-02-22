package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.BillingPeriod;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "billingPeriod")
public class BillingPeriodEntity {
    @Id
    @NotNull
    private LocalDate billingPeriodStart;
    @NotNull
    private LocalDate dueDate;
    @NotNull
    private LocalDate createdDate;

    public static BillingPeriodEntity fromModel(BillingPeriod billingPeriod) {
        final var entity = new BillingPeriodEntity();
        entity.setBillingPeriodStart(billingPeriod.getBillingPeriodStart());
        entity.setDueDate(billingPeriod.getDueDate());
        entity.setCreatedDate(billingPeriod.getCreatedDate());
        return entity;
    }

    public BillingPeriod toModel() {
        return new BillingPeriod(billingPeriodStart, dueDate, createdDate);
    }
}
