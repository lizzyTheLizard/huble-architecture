package site.gutschi.humble.spring.tasks.api;

import jakarta.validation.constraints.NotBlank;

public record CommentTaskRequest(@TaskKeyConstraint String taskKey, @NotBlank String comment) {
}
