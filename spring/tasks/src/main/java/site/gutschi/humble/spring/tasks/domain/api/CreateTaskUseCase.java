package site.gutschi.humble.spring.tasks.domain.api;

import site.gutschi.humble.spring.tasks.model.Task;

public interface CreateTaskUseCase {
    Task create(CreateTaskRequest request);
}

