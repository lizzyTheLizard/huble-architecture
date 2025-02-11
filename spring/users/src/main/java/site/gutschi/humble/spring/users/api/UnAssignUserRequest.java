package site.gutschi.humble.spring.users.api;

public record UnAssignUserRequest(String userEmail, String projectKey) {
}
