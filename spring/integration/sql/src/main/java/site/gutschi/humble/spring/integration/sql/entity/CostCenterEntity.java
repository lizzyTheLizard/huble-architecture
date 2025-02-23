package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "costCenter")
public class CostCenterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private boolean deleted;
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    private List<String> address;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "project_key"))
    private Set<ProjectEntity> projects;

    public static CostCenterEntity fromModel(CostCenter costCenter, ProjectEntityRepository projectEntityRepository) {
        final var entity = new CostCenterEntity();
        entity.setId(costCenter.getId());
        entity.setDeleted(costCenter.isDeleted());
        entity.setEmail(costCenter.getEmail());
        entity.setName(costCenter.getName());
        entity.setAddress(costCenter.getAddress());
        entity.setProjects(costCenter.getProjects().stream()
                .map(p -> projectEntityRepository.getReferenceById(p.getKey()))
                .collect(Collectors.toSet()));
        return entity;
    }

    public CostCenter toModel() {
        final var address = Collections.unmodifiableList(this.address);
        final var projects = this.projects.stream()
                .map(ProjectEntity::toModel)
                .collect(Collectors.toSet());
        return CostCenter.builder()
                .id(id)
                .name(name)
                .address(address)
                .email(email)
                .deleted(deleted)
                .projects(projects)
                .build();
    }
}
