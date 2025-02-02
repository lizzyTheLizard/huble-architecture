package site.gutschi.humble.spring.tasks.domain.api;

import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Collection;

public interface GetTasksUseCase {
    GetTasksResponse getTaskByKey(@TaskKeyConstraint String taskKey);

    Collection<Task> getTasks(GetTasksRequest request);

    int count(GetTasksRequest request);
}
