package site.gutschi.humble.spring.tasks.api;

import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Set;

public interface CreateTaskUseCase {
    Set<Project> getProjectsToCreate();

    Task create(CreateTaskRequest request);
}

