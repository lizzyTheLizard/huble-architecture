package site.gutschi.humble.spring.users.ports;

import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * Save a changed or new user
     *
     * @param user The user to save
     */
    void save(User user);

    /**
     * Find a user by its email
     *
     * @param email The email of the user
     * @return The user or empty if not found
     */
    Optional<User> findByMail(String email);
}
