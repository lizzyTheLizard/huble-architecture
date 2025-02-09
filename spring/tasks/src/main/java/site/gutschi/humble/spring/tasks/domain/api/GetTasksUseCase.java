package site.gutschi.humble.spring.tasks.domain.api;

public interface GetTasksUseCase {
    GetTaskResponse getTaskByKey(@TaskKeyConstraint String taskKey);

    FindTasksResponse findTasks(FindTasksRequest request);
}
