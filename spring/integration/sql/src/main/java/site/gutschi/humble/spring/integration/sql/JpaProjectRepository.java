package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.ProjectEntity;
import site.gutschi.humble.spring.integration.sql.entity.ProjectHistoryEntryEntity;
import site.gutschi.humble.spring.integration.sql.entity.ProjectRoleEntity;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectHistoryEntry;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaProjectRepository implements ProjectRepository {
    private final ProjectEntityRepository projectEntityRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void save(Project project) {
        final var entity = fromModel(project);
        projectEntityRepository.save(entity);
    }

    @Override
    public Optional<Project> findByKey(String key) {
        return projectEntityRepository.findById(key)
                .map(this::toModel);
    }

    @Override
    public Set<Project> findAll() {
        return projectEntityRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Project> findAllForUser(User user) {
        final var userEntity = jpaUserRepository.fromModel(user);
        return projectEntityRepository.findByUser(userEntity).stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    public Project toModel(ProjectEntity entity) {
        final var projectRoles = entity.getProjectRoles().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        final var historyEntries = entity.getHistoryEntries().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        return Project.builder()
                .key(entity.getKey())
                .name(entity.getName())
                .active(entity.isActive())
                .estimations(entity.getEstimations())
                .projectRoles(projectRoles)
                .historyEntries(historyEntries)
                .build();
    }

    public ProjectEntity fromModel(Project project) {
        final var result = new ProjectEntity();
        result.setKey(project.getKey());
        result.setName(project.getName());
        result.setActive(project.isActive());
        result.setEstimations(project.getEstimations());
        result.setProjectRoles(project.getProjectRoles().stream()
                .map(e -> fromModel(e, result))
                .collect(Collectors.toSet()));
        result.setHistoryEntries(project.getHistoryEntries().stream()
                .map(h -> fromModel(h, result))
                .collect(Collectors.toSet()));
        return result;
    }


    public ProjectHistoryEntryEntity fromModel(ProjectHistoryEntry projectHistoryEntry, ProjectEntity project) {
        final var result = new ProjectHistoryEntryEntity();
        result.setUser(jpaUserRepository.fromModel(projectHistoryEntry.user()));
        result.setProject(project);
        result.setTimestamp(projectHistoryEntry.timestamp());
        result.setType(projectHistoryEntry.type());
        if (projectHistoryEntry.affectedUser() != null)
            result.setAffectedUser(jpaUserRepository.fromModel(projectHistoryEntry.affectedUser()));
        result.setOldValue(projectHistoryEntry.oldValue());
        result.setNewValue(projectHistoryEntry.newValue());
        return result;
    }

    public ProjectHistoryEntry toModel(ProjectHistoryEntryEntity entity) {
        return ProjectHistoryEntry.builder()
                .user(jpaUserRepository.toModel(entity.getUser()))
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .affectedUser(entity.getAffectedUser() != null ? jpaUserRepository.toModel(entity.getAffectedUser()) : null)
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .build();
    }


    public ProjectRoleEntity fromModel(ProjectRole projectRole, ProjectEntity project) {
        final var entity = new ProjectRoleEntity();
        entity.setProject(project);
        entity.setUser(jpaUserRepository.fromModel(projectRole.user()));
        entity.setType(projectRole.type());
        return entity;
    }

    public ProjectRole toModel(ProjectRoleEntity entity) {
        return new ProjectRole(jpaUserRepository.toModel(entity.getUser()), entity.getType());
    }

}
