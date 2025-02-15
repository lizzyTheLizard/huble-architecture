package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Entity(name = "project")
public class ProjectEntity {
    @Id
    @NotBlank
    private String key;
    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<Integer> estimations;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private Collection<ProjectRoleEntity> projectRoles;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private Collection<ProjectHistoryEntryEntity> historyEntries;
    @NotBlank
    private String name;
    private boolean active;

    public static ProjectEntity fromModel(Project project, UserEntityRepository repository) {
        final var result = new ProjectEntity();
        result.setKey(project.getKey());
        result.setName(project.getName());
        result.setActive(project.isActive());
        result.setEstimations(project.getEstimations());
        result.setProjectRoles(project.getProjectRoles().stream()
                .map(e -> ProjectRoleEntity.fromModel(e, result))
                .collect(Collectors.toList()));
        result.setHistoryEntries(project.getHistoryEntries().stream()
                .map(h -> ProjectHistoryEntryEntity.fromModel(h, result, repository))
                .collect(Collectors.toList()));
        return result;
    }

    public Project toModel() {
        final var projectRoles = this.projectRoles.stream()
                .map(ProjectRoleEntity::toModel)
                .collect(Collectors.toList());
        final var historyEntries = this.historyEntries.stream()
                .map(ProjectHistoryEntryEntity::toModel)
                .collect(Collectors.toList());
        return Project.builder()
                .key(this.key)
                .name(this.name)
                .active(this.active)
                .estimations(this.estimations)
                .projectRoles(projectRoles)
                .historyEntries(historyEntries)
                .build();
    }
}
