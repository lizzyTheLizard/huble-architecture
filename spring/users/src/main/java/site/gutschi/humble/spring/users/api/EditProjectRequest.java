package site.gutschi.humble.spring.users.api;

public record EditProjectRequest(String projectKey,
                                 String name,
                                 boolean active) {
}
