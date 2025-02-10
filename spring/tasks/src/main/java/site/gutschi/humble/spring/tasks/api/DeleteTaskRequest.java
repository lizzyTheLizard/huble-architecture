package site.gutschi.humble.spring.tasks.api;

public record DeleteTaskRequest(@TaskKeyConstraint String taskKey) {
}
