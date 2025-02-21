package site.gutschi.humble.spring.tasks.ports;

import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.usecases.GetTasksUseCase;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public interface SearchCaller {
    SearchCallerResponse findTasks(SearchCallerRequest request);

    void informUpdatedTasks(Task... tasks);

    void informDeletedTasks(Task... tasks);

    void clear();

    record SearchCallerRequest(String query, int page, int pageSize, Collection<Project> allowedProjects) {
    }

    record SearchCallerResponse(Collection<GetTasksUseCase.TaskFindView> tasks, int total) {
    }
}
