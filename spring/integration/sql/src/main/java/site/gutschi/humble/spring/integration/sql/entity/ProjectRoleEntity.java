package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Getter
@Setter
@Entity(name = "projectRole")
public class ProjectRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private ProjectEntity project;
    @Enumerated(EnumType.STRING)
    private ProjectRoleType type;

    static ProjectRoleEntity fromModel(ProjectRole projectRole, ProjectEntity project) {
        final var entity = new ProjectRoleEntity();
        entity.setProject(project);
        entity.setUser(UserEntity.fromModel(projectRole.user()));
        entity.setType(projectRole.type());
        return entity;
    }

    public ProjectRole toModel() {
        return new ProjectRole(user.toModel(), type);
    }
}
