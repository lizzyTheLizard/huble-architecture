package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.tasks.domain.api.GetTasksRequest;
import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findByKey(String taskKey);

    void save(Task existingTask);

    Collection<Task> findTasks(GetTasksRequest request);

    Collection<Task> findTasksWithoutPaging(GetTasksRequest request);

    int nextId(String projectKey);
}
