package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;

//TODO: Create test cases
public interface GetUserUseCase {
    Optional<User> getUser(String userEmail);
}
