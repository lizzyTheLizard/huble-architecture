package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.ManageUserNotAllowedException;
import site.gutschi.humble.spring.users.api.UserNotVisibleException;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

@Service
@RequiredArgsConstructor
public class CanAccessUserPolicy {
    private final CurrentUserApi currentUserApi;
    private final ProjectRepository projectRepository;

    public void ensureCanCreate(User user) {
        if (currentUserApi.isSystemAdmin()) return;
        final var isProjectAdmin = projectRepository.findAll().stream()
                .flatMap(p -> p.getRole(currentUserApi.currentEmail()).stream())
                .anyMatch(ProjectRoleType::canManage);
        if (isProjectAdmin) return;
        throw new ManageUserNotAllowedException(user.getEmail());
    }

    public void ensureCanEdit(User user) {
        if (currentUserApi.isSystemAdmin()) return;
        final var currentUser = currentUserApi.currentEmail();
        if (currentUser.equals(user.getEmail())) return;
        if (!inSameProject(user)) throw new UserNotVisibleException(user.getEmail());
        throw new ManageUserNotAllowedException(user.getEmail());
    }

    public void ensureCanRead(User user) {
        if (currentUserApi.isSystemAdmin()) return;
        final var currentUser = currentUserApi.currentEmail();
        if (currentUser.equals(user.getEmail())) return;
        if (inSameProject(user)) return;
        throw new UserNotVisibleException(user.getEmail());
    }

    private boolean inSameProject(User user) {
        final var currentUser = currentUserApi.currentEmail();
        return projectRepository.findAllForUser(user).stream()
                .flatMap(p -> p.getRole(currentUser).stream())
                .anyMatch(ProjectRoleType::canRead);
    }
}
