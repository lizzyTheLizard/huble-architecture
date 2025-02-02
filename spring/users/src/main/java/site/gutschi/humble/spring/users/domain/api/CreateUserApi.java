package site.gutschi.humble.spring.users.domain.api;

import site.gutschi.humble.spring.users.model.User;

public interface CreateUserApi {
    User createUser(CreateUserRequest request);
}
