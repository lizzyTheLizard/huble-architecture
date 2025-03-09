package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity(name = "projectBill")
public class ProjectBillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private ProjectEntity project;
    @NotNull
    @PositiveOrZero
    private BigDecimal amount;
    @PositiveOrZero
    private long totalNonDeletedTasks;
    @PositiveOrZero
    private long createdTasks;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private BillEntity bill;
}
