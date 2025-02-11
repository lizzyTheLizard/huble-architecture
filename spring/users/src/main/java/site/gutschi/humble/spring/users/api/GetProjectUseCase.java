package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public interface GetProjectUseCase {
    GetProjectResponse getProject(String projectKey);

    Collection<Project> getAllProjects();
}
