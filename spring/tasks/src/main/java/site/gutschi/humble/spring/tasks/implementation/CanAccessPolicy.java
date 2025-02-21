package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanDeleteTasksInProject(Project project) {
        if (!canDeleteTasksInProject(project)) {
            throw NotAllowedException.notAllowed("Project", project.getKey(), "delete task", currentUserApi.currentEmail());
        }
    }

    public boolean canDeleteTasksInProject(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentEmail = currentUserApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public void ensureCanEditTasksInProject(Project project) {
        if (!canEditTasksInProject(project)) {
            throw NotAllowedException.notAllowed("Project", project.getKey(), "edit task", currentUserApi.currentEmail());
        }
    }

    public boolean canEditTasksInProject(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentEmail = currentUserApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canWrite)
                .orElse(false);
    }
}
