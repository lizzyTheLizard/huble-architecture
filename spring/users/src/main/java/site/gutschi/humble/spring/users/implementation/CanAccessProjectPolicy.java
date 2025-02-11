package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.ManageProjectNotAllowedException;
import site.gutschi.humble.spring.users.api.ProjectNotVisibleException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessProjectPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanManage(Project project) {
        if (currentUserApi.isSystemAdmin()) return;
        final var currentUser = currentUserApi.currentEmail();
        final var projectRoleType = getProjectRoleType(currentUser, project);
        if (projectRoleType.canManage()) return;
        throw new ManageProjectNotAllowedException(project.getKey());
    }

    public void ensureCanRead(Project project) {
        if (canRead(project)) return;
        final var currentUser = currentUserApi.currentEmail();
        throw new ProjectNotVisibleException(currentUser, project.getKey());
    }

    public boolean canRead(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canRead)
                .orElse(false);
    }

    private ProjectRoleType getProjectRoleType(String currentUser, Project project) {
        return project.getRole(currentUser)
                .orElseThrow(() -> new ProjectNotVisibleException(currentUser, project.getKey()));
    }
}
