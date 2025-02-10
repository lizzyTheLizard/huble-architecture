package site.gutschi.humble.spring.tasks.ports;

import site.gutschi.humble.spring.tasks.model.Task;

public interface SearchCaller {
    SearchCallerResponse findTasks(SearchCallerRequest request);

    void informUpdatedTasks(Task... tasks);

    void clear();
}
