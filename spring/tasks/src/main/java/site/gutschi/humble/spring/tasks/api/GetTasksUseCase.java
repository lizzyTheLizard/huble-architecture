package site.gutschi.humble.spring.tasks.api;

public interface GetTasksUseCase {
    GetTaskResponse getTaskByKey(@TaskKeyConstraint String taskKey);

    FindTasksResponse findTasks(FindTasksRequest request);
}
