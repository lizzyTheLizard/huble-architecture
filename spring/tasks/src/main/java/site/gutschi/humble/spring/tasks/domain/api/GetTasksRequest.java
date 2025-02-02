package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Collection;
import java.util.Map;

public record GetTasksRequest(
        @NotBlank String project, @NotBlank String assignee, Collection<TaskStatus> statuses,
        String text, Map<String, Boolean> order, int page, int pageSize) {
}
