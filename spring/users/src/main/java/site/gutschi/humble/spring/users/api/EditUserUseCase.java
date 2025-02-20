package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.User;

public interface EditUserUseCase {
    /**
     * Edits a user
     * <p> Checks if the user is allowed to edit the user and then edits the user.
     *
     * @param request The request object
     * @throws NotFoundException        if the user is not found or not visible
     * @throws IllegalArgumentException if the input is not valid
     */
    void editUser(EditUserRequest request);

    /**
     * Updates the user after the login or create it if this is the first login.
     * Only the user itself can do this.
     *
     * @param request The request object
     * @return The created / updated user
     */
    User updateUserAfterLogin(UpdateUserRequest request);

    /**
     * The request object for editing a user
     *
     * @param name  The new name of the user
     * @param email The new email of the user
     */
    record EditUserRequest(String name, String email) {
    }

    /**
     * The request object for registering a user
     *
     * @param email The email of the user
     * @param name  The name of the user
     */
    record UpdateUserRequest(String email, String name) {
    }
}
