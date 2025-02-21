package site.gutschi.humble.spring.users.usecases;

import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;

public interface CreateProjectUseCase {

    /**
     * Creates a new project
     * <p>
     * Checks if the user is allowed to create a project, and the input is valid,
     * then creates the project and return it.
     *
     * @return the created project
     * @throws NotAllowedException   if the user is not allowed to create a project
     * @throws InvalidInputException If the key is not unique
     */
    Project createProject(CreateProjectRequest request);

    /**
     * Checks if the user is allowed to create a project
     *
     * @return true if the user is allowed to create a project
     */
    boolean canCreateProject();

    /**
     * The request object for creating a project
     *
     * @param key  The key of the project to create
     * @param name The name of the project to create
     */
    record CreateProjectRequest(String key, String name) {
    }
}
