package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class ProjectNotVisibleException extends RuntimeException {
    private final String projectKey;

    public ProjectNotVisibleException(String userEmail, String projectKey) {
        super("Project '" + projectKey + "' is not visible for user '" + userEmail + "'");
        this.projectKey = projectKey;
    }
}
