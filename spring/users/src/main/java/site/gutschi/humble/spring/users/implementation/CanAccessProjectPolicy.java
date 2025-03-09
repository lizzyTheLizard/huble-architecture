package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;

@Service
@RequiredArgsConstructor
public class CanAccessProjectPolicy {
    private final CurrentUserInformation currentUserInformation;

    public void ensureCanCreate() {
        final var currentUser = currentUserInformation.getCurrentUser();
        if (canCreate()) return;
        throw NotAllowedException.notAllowed("Project", "create", currentUser.getEmail());
    }

    public void ensureCanManage(Project project) {
        final var currentUser = currentUserInformation.getCurrentUser();
        ensureCanRead(project);
        if (canManage(project)) return;
        throw NotAllowedException.notAllowed("Project", project.getKey(), "edit", currentUser.getEmail());
    }

    public void ensureCanRead(Project project) {
        final var currentUser = currentUserInformation.getCurrentUser();
        if (canRead(project)) return;
        throw NotFoundException.notVisible("Project", project.getKey(), currentUser.getEmail());
    }

    public NotFoundException projectNotFound(String projectKey) {
        final var currentUser = currentUserInformation.getCurrentUser();
        throw NotFoundException.notFound("Project", projectKey, currentUser.getEmail());
    }

    public boolean canRead(Project project) {
        if (currentUserInformation.isSystemAdmin()) return true;
        final var currentUser = currentUserInformation.getCurrentUser();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canRead)
                .orElse(false);
    }

    public boolean canManage(Project project) {
        if (currentUserInformation.isSystemAdmin()) return true;
        final var currentUser = currentUserInformation.getCurrentUser();
        return project.getRole(currentUser)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public boolean canCreate() {
        return currentUserInformation.isSystemAdmin();
    }
}
