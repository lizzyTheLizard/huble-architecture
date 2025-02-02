package site.gutschi.humble.spring.users.domain.api;

public record EditProjectRequest(String projectKey,
                                 String name,
                                 boolean active) {
}
