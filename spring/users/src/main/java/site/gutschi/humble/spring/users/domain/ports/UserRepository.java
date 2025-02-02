package site.gutschi.humble.spring.users.domain.ports;

import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);

    Optional<User> findByMail(String email);

    Collection<User> getUserForProject(String projectKey);
}
