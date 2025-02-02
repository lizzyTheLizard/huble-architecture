package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Singular;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Map;

@Builder
public record EditTaskRequest(@TaskKeyConstraint String taskKey, @NotBlank String title, @NotNull String description,
                              @NotNull TaskStatus status,
                              @Singular @NotNull Map<String, String> additionalFields,
                              String assignee, @Positive Integer estimation) {
}
