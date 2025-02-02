package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotBlank;

public record CommentTaskRequest(@TaskKeyConstraint String taskKey, @NotBlank String comment) {
}
