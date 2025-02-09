package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessPolicy {
    private final UserApi userApi;

    public void ensureCanDeleteTasksInProject(Project project) {
        if (!canDeleteTasksInProject(project)) {
            throw new NotAllowedException("You are not allowed to delete tasks in projectKey '" + project.getKey() + "'");
        }
    }

    public boolean canDeleteTasksInProject(Project project) {
        if (userApi.isSystemAdmin()) return true;
        final var currentEmail = userApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public void ensureCanEditTasksInProject(Project project) {
        if (!canEditTasksInProject(project)) {
            throw new NotAllowedException("You are not allowed to write projectKey '" + project.getKey() + "'");
        }
    }

    public boolean canEditTasksInProject(Project project) {
        if (userApi.isSystemAdmin()) return true;
        final var currentEmail = userApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canWrite)
                .orElse(false);
    }
}
