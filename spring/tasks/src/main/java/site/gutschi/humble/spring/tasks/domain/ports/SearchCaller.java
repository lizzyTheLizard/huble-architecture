package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.tasks.model.Task;

import java.util.Collection;
import java.util.stream.Collectors;

public interface SearchCaller {
    SearchCallerResponse findTasks(SearchCallerRequest request);

    void informUpdatedTasks(Task ...tasks);

    void clear();
}
