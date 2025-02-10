package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.User;

//TODO: Create test cases
public interface CreateUserUseCase {
    User createUser(CreateUserRequest request);
}
