package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.User;

public interface CreateUserUseCase {
    User createUser(CreateUserRequest request);
}
