package site.gutschi.humble.spring.users.domain.api;

import site.gutschi.humble.spring.users.model.ProjectRoleType;

public record AssignUserRequest(String userEmail, String projectKey, ProjectRoleType type) {
}
