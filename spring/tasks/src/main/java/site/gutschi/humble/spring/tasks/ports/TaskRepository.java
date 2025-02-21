package site.gutschi.humble.spring.tasks.ports;

import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Optional;

public interface TaskRepository {
    /**
     * Find a task by its key
     *
     * @param taskKey The key of the task
     * @return The task or empty if task does not exist
     */
    Optional<Task> findByKey(String taskKey);

    /**
     * Save an existing or new task
     *
     * @param existingTask The task to save
     */
    void save(Task existingTask);

    /**
     * The nextID for a new task in a given project. This always return the nextID and increments the internal counter.
     *
     * @param projectKey The key of the project
     * @return The next ID
     */
    int nextId(String projectKey);
}
