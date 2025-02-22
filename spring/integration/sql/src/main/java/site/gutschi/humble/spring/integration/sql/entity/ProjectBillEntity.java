package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.math.BigDecimal;

@Getter
@Setter
@Entity(name = "projectBill")
public class ProjectBillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @NotNull
    private ProjectEntity project;
    @NotNull
    @PositiveOrZero
    private BigDecimal amount;
    @PositiveOrZero
    private int totalNonDeletedTasks;
    @PositiveOrZero
    private int createdTasks;

    public static ProjectBillEntity fromModel(ProjectBill pb, ProjectEntityRepository projectEntityRepository) {
        final var entity = new ProjectBillEntity();
        entity.setProject(projectEntityRepository.getReferenceById(pb.getProject().getKey()));
        entity.setAmount(pb.getAmount());
        entity.setTotalNonDeletedTasks(pb.getTotalNonDeletedTasks());
        entity.setCreatedTasks(pb.getCreatedTasks());
        return entity;
    }

    public ProjectBill toModel() {
        final var project = this.project.toModel();
        return new ProjectBill(project, amount, totalNonDeletedTasks, createdTasks);
    }
}
