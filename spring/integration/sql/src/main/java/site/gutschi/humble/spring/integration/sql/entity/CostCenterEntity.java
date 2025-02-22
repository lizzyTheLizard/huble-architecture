package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "costCenter")
public class CostCenterEntity {
    @Id
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private boolean active;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> address;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<ProjectEntity> projects;

    public static CostCenterEntity fromModel(CostCenter costCenter) {
        final var entity = new CostCenterEntity();
        entity.setId(costCenter.getId());
        entity.setActive(costCenter.isActive());
        entity.setEmail(costCenter.getEmail());
        entity.setName(costCenter.getName());
        return entity;
    }

    public CostCenter toModel() {
        final var address = Collections.unmodifiableList(this.address);
        final var projects = this.projects.stream()
                .map(ProjectEntity::toModel)
                .collect(Collectors.toSet());
        return new CostCenter(id, name, address, email, active, projects);
    }
}
