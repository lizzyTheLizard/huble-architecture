package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity(name = "project")
public class ProjectEntity {
    @Id
    @NotBlank
    private String key;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> estimations;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private Set<ProjectRoleEntity> projectRoles;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private Set<ProjectHistoryEntryEntity> historyEntries;
    @NotBlank
    private String name;
    private boolean active;
}
