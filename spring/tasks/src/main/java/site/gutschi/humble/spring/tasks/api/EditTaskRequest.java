package site.gutschi.humble.spring.tasks.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

@Builder
public record EditTaskRequest(@TaskKeyConstraint String taskKey, @NotBlank String title, @NotNull String description,
                              @NotNull TaskStatus status,
                              String assignee, @Positive Integer estimation) {
}
