package site.gutschi.humble.spring.users.ports;

import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    Optional<Project> findByKey(String key);

    Collection<Project> findAll();

    Collection<Project> findAllForUser(User user);
}
