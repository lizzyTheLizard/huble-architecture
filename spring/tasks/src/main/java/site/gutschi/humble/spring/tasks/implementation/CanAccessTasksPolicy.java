package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessTasksPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanDeleteTasksInProject(Project project) {
        if (canDeleteTasksInProject(project)) return;
        final var currentUser = currentUserApi.getCurrentUser().getEmail();
        throw NotAllowedException.notAllowed("Project", project.getKey(), "delete task", currentUser);
    }

    public boolean canDeleteTasksInProject(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.getCurrentUser();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public void ensureCanEditTasksInProject(Project project) {
        if (canEditTasksInProject(project)) return;
        final var currentUser = currentUserApi.getCurrentUser().getEmail();
        throw NotAllowedException.notAllowed("Project", project.getKey(), "edit task", currentUser);
    }

    public boolean canEditTasksInProject(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.getCurrentUser();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canWrite)
                .orElse(false);
    }
}
