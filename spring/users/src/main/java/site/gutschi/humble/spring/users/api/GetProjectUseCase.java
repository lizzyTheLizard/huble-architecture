package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;
import java.util.Optional;

//TODO: Create test cases
public interface GetProjectUseCase {
    Optional<Project> getProject(String projectKey);

    Collection<Project> getAllProjects();
}
