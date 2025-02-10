package site.gutschi.humble.spring.tasks.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FindTasksRequest(@NotNull String query, int page, int pageSize) {
}
