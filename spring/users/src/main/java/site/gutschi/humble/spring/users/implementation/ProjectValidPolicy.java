package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class ProjectValidPolicy {
    public void ensureProjectValid(Project project) {
        if (project.getKey() == null || project.getKey().isBlank() || project.getKey().length() > 10) {
            throw new InvalidInputException("Project key must not be empty and must not exceed 10 characters");
        }
        if (project.getName() == null || project.getName().isBlank()) {
            throw new InvalidInputException("Project name must not be empty");
        }
        if (project.getEstimations().isEmpty()) {
            throw new InvalidInputException("Project must have at least one estimation");
        }
        final var admins = project.getProjectRoles().stream()
                .map(ProjectRole::type)
                .filter(ProjectRoleType.ADMIN::equals)
                .count();
        if (admins == 0) {
            throw new InvalidInputException("Project must have at least one admin");
        }
    }
}
