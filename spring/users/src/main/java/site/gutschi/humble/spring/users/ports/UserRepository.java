package site.gutschi.humble.spring.users.ports;

import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findByMail(String email);
}
