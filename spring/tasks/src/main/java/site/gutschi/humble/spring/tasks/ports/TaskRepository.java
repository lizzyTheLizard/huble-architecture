package site.gutschi.humble.spring.tasks.ports;

import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findByKey(String taskKey);

    void save(Task existingTask);

    int nextId(String projectKey);
}
