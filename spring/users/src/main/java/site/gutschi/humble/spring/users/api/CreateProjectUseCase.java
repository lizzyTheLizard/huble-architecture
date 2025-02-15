package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.Project;

public interface CreateProjectUseCase {
    Project createProject(CreateProjectRequest request);

    boolean canCreateProject();
}
