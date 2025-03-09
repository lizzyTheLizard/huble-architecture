package site.gutschi.humble.spring.tasks.api;

import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;


public interface CreateTaskUseCase {
    /**
     * Create a new task
     * <p>
     * Checks if the user is allowed to create the task, if the task is valid, creates and saves the task.
     *
     * @return The created task
     * @throws NotAllowedException   If the user is not allowed to create a task in this project.
     * @throws NotFoundException     If the project is not found or not visible.
     * @throws InvalidInputException If the key is not unique.
     */
    Task create(CreateTaskRequest request);

    /**
     * Get a collection of projects for which the user can create tasks.
     */
    Collection<Project> getProjectsToCreate();

    /**
     * A request to create a task
     *
     * @param projectKey  Key of the project to create the task for
     * @param title       The title of the task
     * @param description The description of the task
     */
    record CreateTaskRequest(String projectKey, String title, String description) {
    }
}


