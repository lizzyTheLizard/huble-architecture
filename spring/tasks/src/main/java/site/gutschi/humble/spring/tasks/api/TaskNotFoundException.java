package site.gutschi.humble.spring.tasks.api;

import lombok.Getter;

@Getter
public class TaskNotFoundException extends RuntimeException {
    private final String taskKey;

    public TaskNotFoundException(String taskKey) {
        super("Task '" + taskKey + "' could not be found");
        this.taskKey = taskKey;
    }
}
