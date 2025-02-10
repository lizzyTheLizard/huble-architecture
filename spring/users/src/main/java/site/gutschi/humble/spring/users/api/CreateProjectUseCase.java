package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.Project;

//TODO: Create test cases
public interface CreateProjectUseCase {
    Project createProject(CreateProjectRequest request);
}
