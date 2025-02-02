package site.gutschi.humble.spring.users.domain.api;

import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.Optional;

public interface GetUserApi {
    Optional<User> getUser(String userEmail);

    Collection<User> getUserForProject(String projectKey);
}
