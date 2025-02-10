package site.gutschi.humble.spring.tasks.api;

import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public interface CreateTaskUseCase {
    Collection<Project> getProjectsToCreate();

    Task create(CreateTaskRequest request);
}

