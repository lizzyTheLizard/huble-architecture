package site.gutschi.humble.spring.tasks.api;

import lombok.Getter;

@Getter
public class TaskDeletedException extends RuntimeException {
    private final String taskKey;

    public TaskDeletedException(String taskKey) {
        super("Task '" + taskKey + "' has been deleted");
        this.taskKey = taskKey;
    }
}
