package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequest(@TaskKeyConstraint String projectKey,
                                @NotBlank String title,
                                @NotNull String description) {
}
