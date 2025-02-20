package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.users.model.ProjectHistoryEntry;
import site.gutschi.humble.spring.users.model.ProjectHistoryType;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "projectHistoryEntry")
public class ProjectHistoryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private ProjectEntity project;
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private ProjectHistoryType type;
    @ManyToOne
    private UserEntity affectedUser;
    private String oldValue;
    private String newValue;

    public static ProjectHistoryEntryEntity fromModel(ProjectHistoryEntry projectHistoryEntry, ProjectEntity project, UserEntityRepository repository) {
        final var result = new ProjectHistoryEntryEntity();
        result.setUser(repository.getReferenceById(projectHistoryEntry.user()));
        result.setProject(project);
        result.setTimestamp(projectHistoryEntry.timestamp());
        result.setType(projectHistoryEntry.type());
        if (projectHistoryEntry.affectedUser() != null)
            result.setAffectedUser(repository.getReferenceById(projectHistoryEntry.affectedUser()));
        result.setOldValue(projectHistoryEntry.oldValue());
        result.setNewValue(projectHistoryEntry.newValue());
        return result;
    }

    public ProjectHistoryEntry toModel() {
        return ProjectHistoryEntry.builder()
                .user(user.getEmail())
                .timestamp(timestamp)
                .type(type)
                .affectedUser(affectedUser != null ? affectedUser.getEmail() : null)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }
}
