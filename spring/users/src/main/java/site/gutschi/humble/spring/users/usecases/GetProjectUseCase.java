package site.gutschi.humble.spring.users.usecases;

import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Set;

public interface GetProjectUseCase {

    /**
     * Get a project by its key
     * <p>
     * Checks if the user is allowed to view the project, then returns the project.
     *
     * @param projectKey The key of the project to get
     * @return The project and extra information
     * @throws NotFoundException If the project does not exist or is invisible to the user.
     */
    GetProjectResponse getProject(String projectKey);

    /**
     * Get all projects the user is allowed to access
     *
     * @return All projects the user is allowed to manage.
     */
    Set<Project> getAllProjects();

    /**
     * The response object for getting a project
     *
     * @param project    The project
     * @param manageable If the user is allowed to manage the project
     */
    record GetProjectResponse(Project project, boolean manageable) {
    }
}
