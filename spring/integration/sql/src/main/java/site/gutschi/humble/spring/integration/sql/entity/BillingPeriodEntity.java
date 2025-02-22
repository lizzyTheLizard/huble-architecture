package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate dueDate;
    @NotNull
    private LocalDate createdDate;

    public static BillingPeriodEntity fromModel(BillingPeriod billingPeriod) {
        final var entity = new BillingPeriodEntity();
        entity.setId(billingPeriod.id());
        entity.setStart(billingPeriod.start());
        entity.setDueDate(billingPeriod.dueDate());
        entity.setCreatedDate(billingPeriod.createdDate());
        return entity;
    }

    public BillingPeriod toModel() {
        return new BillingPeriod(id, start, dueDate, createdDate);
    }
}
