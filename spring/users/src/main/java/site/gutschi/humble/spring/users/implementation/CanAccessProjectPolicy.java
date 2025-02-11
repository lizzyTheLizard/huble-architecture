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
        final var currentUser = currentUserApi.currentEmail();
        if (!canRead(project)) throw new ProjectNotVisibleException(currentUser, project.getKey());
        if (!canManage(project)) throw new ManageProjectNotAllowedException(project.getKey());
    }

    public void ensureCanRead(Project project) {
        final var currentUser = currentUserApi.currentEmail();
        if (canRead(project)) return;
        throw new ProjectNotVisibleException(currentUser, project.getKey());
    }

    public boolean canRead(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canRead)
                .orElse(false);
    }

    public boolean canManage(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        final var projectRoleType = getProjectRoleType(currentUser, project);
        return projectRoleType.canManage();
    }

    private ProjectRoleType getProjectRoleType(String currentUser, Project project) {
        return project.getRole(currentUser)
                .orElseThrow(() -> new ProjectNotVisibleException(currentUser, project.getKey()));
    }

}
