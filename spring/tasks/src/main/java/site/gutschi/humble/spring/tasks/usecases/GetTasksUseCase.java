package site.gutschi.humble.spring.tasks.usecases;

import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Set;

public interface GetTasksUseCase {
    /**
     * Get all tasks for a project
     */
    //TODO Modelling: Use Project instead of String
    //TODO API: Separate API and UseCse
    Set<Task> getTasksForProject(String projectKey);
}
