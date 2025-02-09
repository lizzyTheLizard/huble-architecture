package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Collection;
import java.util.Map;

@Builder
public record FindTasksRequest(String query, int page, int pageSize) {
}
