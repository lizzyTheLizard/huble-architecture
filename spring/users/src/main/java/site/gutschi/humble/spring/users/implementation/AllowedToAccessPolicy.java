package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.ManageProjectNotAllowedException;
import site.gutschi.humble.spring.users.api.ManageUserNotAllowedException;
import site.gutschi.humble.spring.users.api.ProjectNotVisibleException;
import site.gutschi.humble.spring.users.api.UserNotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
public class AllowedToAccessPolicy {
    private final CurrentUserApi currentUserApi;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public void ensureCanManage(Project project) {
        final var currentUser = getCurrentUser();
        if (currentUser.isSystemAdmin()) return;
        final var projectRoleType = getProjectRoleType(currentUser, project);
        if (!projectRoleType.canManage()) {
            throw new ManageProjectNotAllowedException(project.getKey());
        }
    }

    public void ensureCanEdit(User user) {
        final var currentUser = getCurrentUser();
        if (currentUser.isSystemAdmin()) return;
        if (!currentUser.getEmail().equals(user.getEmail())) {
            throw new ManageUserNotAllowedException(user.getEmail());
        }
    }

    public boolean canRead(Project project) {
        final var currentUser = getCurrentUser();
        if (currentUser.isSystemAdmin()) return true;
        return project.getProjectRoles().stream()
                .filter(role -> role.user().getEmail().equals(currentUser.getEmail()))
                .anyMatch(role -> role.type().canRead());
    }

    public boolean canRead(User user) {
        final var currentUser = getCurrentUser();
        if (currentUser.isSystemAdmin()) return true;
        if (currentUser.getEmail().equals(user.getEmail())) return true;
        return projectRepository.findAllForUser(user).stream()
                .anyMatch(this::canRead);
    }

    private User getCurrentUser() {
        final var currentEmail = currentUserApi.currentEmail();
        return userRepository.findByMail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException(currentEmail));
    }

    private ProjectRoleType getProjectRoleType(User user, Project project) {
        return project.getRole(user.getEmail())
                .orElseThrow(() -> new ProjectNotVisibleException(user.getEmail(), project.getKey()));
    }
}
