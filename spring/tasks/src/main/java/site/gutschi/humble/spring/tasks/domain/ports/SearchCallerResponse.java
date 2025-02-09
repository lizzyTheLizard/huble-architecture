package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.tasks.domain.api.FindTasksResponse;

import java.util.Collection;

public record SearchCallerResponse(Collection<FindTasksResponse.TaskFindView> tasks, int total) {
}

