package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Set;

public interface EditProjectUseCase {
    /**
     * Edit an existing project
     * <p>
     * Checks if the project exists, and the user has the right to edit the project. Then updates the project.
     *
     * @param request The request to edit the project
     * @throws NotFoundException        if the project does not exist or is invisible to the user.
     * @throws IllegalArgumentException if the input is not valid
     */
    void editProject(EditProjectRequest request);

    /**
     * Assign a user to a project
     * <p>
     * Checks if the project and user exists, and the user has the right to edit the project. Then assigns the user to the project.
     *
     * @param request The request to assign the user
     * @throws NotFoundException if the project or user does not exist or is invisible to the user.
     */
    void assignUser(AssignUserRequest request);

    /**
     * Unassign a user from a project
     * <p>
     * Checks if the project and user exists, and the user has the right to edit the project. Then unassigns the user from the project.
     *
     * @param request The request to unassign the user
     * @throws NotFoundException if the project or user does not exist or is invisible to the user.
     */
    void unAssignUser(UnAssignUserRequest request);

    /**
     * Request to edit a project
     *
     * @param projectKey  The key of the project to edit
     * @param name        The new name of the project
     * @param estimations The new estimations of the project
     * @param active      The new active state of the project
     */
    record EditProjectRequest(String projectKey,
                              String name,
                              Set<Integer> estimations,
                              boolean active) {
    }

    /**
     * Request to unassign a user from a project
     *
     * @param userEmail  The email of the user to unassign
     * @param projectKey The key of the project to unassign the user from
     */
    record UnAssignUserRequest(String userEmail, String projectKey) {
    }

    /**
     * Request to assign a user to a project
     *
     * @param userEmail  The email of the user to assign
     * @param projectKey The key of the project to assign the user to
     * @param type       The role of the user in the project
     */
    record AssignUserRequest(String userEmail, String projectKey, ProjectRoleType type) {
    }
}
