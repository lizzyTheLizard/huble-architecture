package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.User;

public interface UpdateUserUseCase {
    /**
     * Updates the user after the login or create it if this is the first login.
     * Only the user itself can do this.
     *
     * @param request The request object
     * @return The created / updated user
     * @throws NotFoundException     If the user was not found.
     * @throws NotAllowedException   If the user tries to update someone else.
     * @throws InvalidInputException If the input is not valid.
     */
    User updateUserAfterLogin(UpdateUserRequest request);

    /**
     * The request object for registering a user
     *
     * @param email The email of the user
     * @param name  The name of the user
     */
    record UpdateUserRequest(String email, String name) {
    }
}
