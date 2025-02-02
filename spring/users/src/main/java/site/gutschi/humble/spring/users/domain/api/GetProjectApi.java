package site.gutschi.humble.spring.users.domain.api;

import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Optional;

public interface GetProjectApi {
    Optional<Project> getProject(String projectKey);
}
