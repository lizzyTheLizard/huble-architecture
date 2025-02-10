package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class ProjectNotFoundException extends RuntimeException {
    private final String projectKey;

    public ProjectNotFoundException(String projectKey) {
        super("Project '" + projectKey + "' could not be found");
        this.projectKey = projectKey;
    }
}
