package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Set;

public interface GetProjectApi {

    /**
     * Get a project by its key
     *
     * @throws NotFoundException If the project does not exist or is invisible to the user.
     */
    Project getProject(String projectKey);

    /**
     * Get all projects the user is allowed to access
     *
     * @return All projects the user is allowed to manage.
     */
    Set<Project> getAllProjects();
}
