package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

@Service
@RequiredArgsConstructor
public class CanAccessUserPolicy {
    private final CurrentUserApi currentUserApi;
    private final ProjectRepository projectRepository;

    public void ensureCanEdit(User user) {
        ensureCanRead(user);
        if (canEdit(user)) return;
        throw NotAllowedException.notAllowed("User", user.getEmail(), "edit", currentUserApi.currentEmail());
    }

    public void ensureCanRead(User user) {
        if (canRead(user)) return;
        final var currentUser = currentUserApi.currentEmail();
        throw NotFoundException.notVisible("User", user.getEmail(), currentUser);
    }

    private boolean canRead(User user) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        if (currentUser.equals(user.getEmail())) return true;
        // Can read if in same project
        return projectRepository.findAllForUser(user).stream()
                .flatMap(p -> p.getRole(currentUser).stream())
                .anyMatch(ProjectRoleType::canRead);
    }

    private boolean canEdit(User user) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        return currentUser.equals(user.getEmail());
    }

    public NotFoundException userNotFound(String email) {
        final var currentUser = currentUserApi.currentEmail();
        throw NotFoundException.notFound("User", email, currentUser);
    }

    public void canUpdateAfterLogin(String email) {
        if (currentUserApi.currentEmail().equals(email)) return;
        if (currentUserApi.isSystemAdmin()) return;
        throw NotAllowedException.notAllowed("User", email, "update", currentUserApi.currentEmail());
    }
}
