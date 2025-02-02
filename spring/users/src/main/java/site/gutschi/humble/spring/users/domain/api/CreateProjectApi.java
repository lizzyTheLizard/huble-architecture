package site.gutschi.humble.spring.users.domain.api;

import site.gutschi.humble.spring.users.model.Project;

public interface CreateProjectApi {
    Project createProject(CreateProjectRequest request);
}
