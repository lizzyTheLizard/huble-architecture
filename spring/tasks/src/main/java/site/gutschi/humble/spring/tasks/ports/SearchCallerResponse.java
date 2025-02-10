package site.gutschi.humble.spring.tasks.ports;

import site.gutschi.humble.spring.tasks.api.FindTasksResponse;

import java.util.Collection;

public record SearchCallerResponse(Collection<FindTasksResponse.TaskFindView> tasks, int total) {
}

