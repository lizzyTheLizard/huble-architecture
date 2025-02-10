package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.User;

//TODO: Create test cases and remove this warning
@SuppressWarnings("unused")
public interface CreateUserUseCase {
    User createUser(CreateUserRequest request);
}
