package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.User;

public interface GetUserUseCase {

    /**
     * Gets a user by email
     *
     * @param userEmail The email of the user
     * @return The user
     * @throws NotFoundException if the user is not found or not visible
     */
    User getUser(String userEmail);
}
