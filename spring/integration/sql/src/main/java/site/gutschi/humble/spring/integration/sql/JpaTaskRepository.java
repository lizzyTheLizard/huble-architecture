package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;
import site.gutschi.humble.spring.integration.sql.entity.TaskEntity;
import site.gutschi.humble.spring.integration.sql.repo.NextIdEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.TaskEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaTaskRepository implements TaskRepository {
    private final TaskEntityRepository taskEntityRepository;
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
        final var entity = TaskEntity.fromModel(task, userEntityRepository);
        taskEntityRepository.save(entity);

    }

    @Override
    public int nextId(String projectKey) {
        final int nextId = nextIdEntityRepository.findById(projectKey)
                .map(NextIdEntity::getNextId)
                .orElse(1);
        final var newEntity = new NextIdEntity();
        newEntity.setProjectKey(projectKey);
        newEntity.setNextId(nextId + 1);
        nextIdEntityRepository.save(newEntity);
        return nextId;
    }
}
