package site.gutschi.humble.spring.tasks.api;

import lombok.Getter;

@Getter
public class EditTaskNotAllowedException extends RuntimeException {
    private final String projectKey;

    public EditTaskNotAllowedException(String projectKey) {
        super("You are not allowed to edit tasks in project '" + projectKey + "'");
        this.projectKey = projectKey;
    }
}
