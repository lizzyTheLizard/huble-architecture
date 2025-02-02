package site.gutschi.humble.spring.tasks.domain.api;

public record DeleteTaskRequest(@TaskKeyConstraint String taskKey) {
}
