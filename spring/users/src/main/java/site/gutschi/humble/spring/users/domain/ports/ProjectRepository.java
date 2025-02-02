package site.gutschi.humble.spring.users.domain.ports;

import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    void save(Project project);

    Optional<Project> findByKey(String key);
}
