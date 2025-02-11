package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.User;

public interface GetUserUseCase {
    User getUser(String userEmail);
}
