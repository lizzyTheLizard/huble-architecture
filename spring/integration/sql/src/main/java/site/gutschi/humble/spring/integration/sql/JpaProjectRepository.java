package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.ProjectEntity;
import site.gutschi.humble.spring.integration.sql.entity.UserEntity;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaProjectRepository implements ProjectRepository {
    private final ProjectEntityRepository projectEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public void save(Project project) {
        final var entity = ProjectEntity.fromModel(project, userEntityRepository);
        projectEntityRepository.save(entity);
    }

    @Override
    public Optional<Project> findByKey(String key) {
        return projectEntityRepository.findById(key)
                .map(ProjectEntity::toModel);
    }

    @Override
    public Collection<Project> findAll() {
        return projectEntityRepository.findAll().stream()
                .map(ProjectEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Project> findAllForUser(User user) {
        final var userEntity = UserEntity.fromModel(user);
        return projectEntityRepository.findByUser(userEntity).stream()
                .map(ProjectEntity::toModel)
                .collect(Collectors.toList());
    }
}
