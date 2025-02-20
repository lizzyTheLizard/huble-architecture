package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessProjectPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanCreate() {
        if (canCreate()) return;
        throw NotAllowedException.notAllowed("Project", "create", currentUserApi.currentEmail());
    }

    public void ensureCanManage(Project project) {
        ensureCanRead(project);
        if (!canManage(project))
            throw NotAllowedException.notAllowed("Project", project.getKey(), "edit", currentUserApi.currentEmail());
    }

    public void ensureCanRead(Project project) {
        if (canRead(project)) return;
        final var currentUser = currentUserApi.currentEmail();
        throw NotFoundException.notVisible("Project", project.getKey(), currentUser);
    }

    public NotFoundException projectNotFound(String projectKey) {
        final var currentUser = currentUserApi.currentEmail();
        throw NotFoundException.notFound("Project", projectKey, currentUser);
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
        return project.getRole(currentUser)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public boolean canCreate() {
        return currentUserApi.isSystemAdmin();
    }
}
