package site.gutschi.humble.spring.users.api;

import site.gutschi.humble.spring.users.model.Project;

public record GetProjectResponse(Project project, boolean manageable) {
}
