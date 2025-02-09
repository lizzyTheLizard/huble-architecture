package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.tasks.domain.api.FindTasksRequest;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findByKey(String taskKey);

    void save(Task existingTask);

    int nextId(String projectKey);
}
