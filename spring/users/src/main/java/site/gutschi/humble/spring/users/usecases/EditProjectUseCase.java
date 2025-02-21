package site.gutschi.humble.spring.users.usecases;

import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Set;

public interface EditProjectUseCase {
    /**
     * Edit an existing project
     * <p>
     * Checks if the project exists, and the user has the right to edit the project. Then updates the project.
     *
     * @throws NotFoundException     If the project does not exist or is invisible to the user.
     * @throws NotAllowedException   If the project is visible, but the user is not allowed to edit it.
     * @throws InvalidInputException If the input is not valid.
     */
    void editProject(EditProjectRequest request);

    /**
     * Assign a user to a project
     * <p>
     * Checks if the project and user exists, and the user has the right to edit the project. Then assigns the user to the project.
     *
     * @throws NotFoundException   If the project or user does not exist or is invisible to the user.
     * @throws NotAllowedException If the project is visible, but the user is not allowed to edit it.
     */
    void assignUser(AssignUserRequest request);

    /**
     * Unassign a user from a project
     * <p>
     * Checks if the project and user exists, and the user has the right to edit the project. Then unassigns the user from the project.
     *
     * @throws NotFoundException   If the project or user does not exist or is invisible to the user.
     * @throws NotAllowedException If the project is visible, but the user is not allowed to edit it.
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
