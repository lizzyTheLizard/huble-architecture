package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class ManageProjectNotAllowedException extends RuntimeException {
    private final String projectKey;

    public ManageProjectNotAllowedException(String projectKey) {
        super("You are not allowed to manage project '" + projectKey + "'");
        this.projectKey = projectKey;
    }
}

