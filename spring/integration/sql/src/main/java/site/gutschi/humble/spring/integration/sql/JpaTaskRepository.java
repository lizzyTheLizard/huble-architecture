package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;
import site.gutschi.humble.spring.integration.sql.entity.TaskEntity;
import site.gutschi.humble.spring.integration.sql.repo.NextIdEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.TaskEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaTaskRepository implements TaskRepository {
    private final TaskEntityRepository taskEntityRepository;
    private final ProjectEntityRepository projectEntityRepository;
    private final NextIdEntityRepository nextIdEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public Optional<Task> findByKey(String taskKey) {
        return taskEntityRepository
                .findById(taskKey)
                .map(TaskEntity::toModel);
    }

    @Override
    public void save(Task task) {
        final var entity = TaskEntity.fromModel(task, userEntityRepository, projectEntityRepository);
        taskEntityRepository.save(entity);

    }

    @Override
    public int nextId(String projectKey) {
        final var name = "PROJECT-" + projectKey;
        final int nextId = nextIdEntityRepository.findById(name)
                .map(NextIdEntity::getNextId)
                .orElse(1);
        final var newEntity = new NextIdEntity();
        newEntity.setName(name);
        newEntity.setNextId(nextId + 1);
        nextIdEntityRepository.save(newEntity);
        return nextId;
    }

    @Override
    public Set<Task> findByProject(Project project) {
        final var projectEntity = projectEntityRepository.getReferenceById(project.getKey());
        return taskEntityRepository.findByProject(projectEntity).stream()
                .map(TaskEntity::toModel)
                .collect(Collectors.toSet());
    }
}
