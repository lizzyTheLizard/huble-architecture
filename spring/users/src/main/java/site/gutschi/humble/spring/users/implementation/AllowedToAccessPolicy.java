package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.ManageProjectNotAllowedException;
import site.gutschi.humble.spring.users.api.ManageUserNotAllowedException;
import site.gutschi.humble.spring.users.api.ProjectNotVisibleException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

@Service
@RequiredArgsConstructor
public class AllowedToAccessPolicy {
    private final CurrentUserApi currentUserApi;
    private final ProjectRepository projectRepository;

    public void ensureCanManage(Project project) {
        if (currentUserApi.isSystemAdmin()) return;
        final var currentUser = currentUserApi.currentEmail();
        final var projectRoleType = getProjectRoleType(currentUser, project);
        if (!projectRoleType.canManage()) {
            throw new ManageProjectNotAllowedException(project.getKey());
        }
    }

    public void ensureCanEdit(User user) {
        if (currentUserApi.isSystemAdmin()) return;
        final var currentUser = currentUserApi.currentEmail();
        if (!currentUser.equals(user.getEmail())) {
            throw new ManageUserNotAllowedException(user.getEmail());
        }
    }

    public boolean canRead(Project project) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        return project.getProjectRoles().stream()
                .filter(role -> role.user().getEmail().equals(currentUser))
                .anyMatch(role -> role.type().canRead());
    }

    public boolean canRead(User user) {
        if (currentUserApi.isSystemAdmin()) return true;
        final var currentUser = currentUserApi.currentEmail();
        if (currentUser.equals(user.getEmail())) return true;
        return projectRepository.findAllForUser(user).stream()
                .anyMatch(this::canRead);
    }

    private ProjectRoleType getProjectRoleType(String currentUser, Project project) {
        return project.getRole(currentUser)
                .orElseThrow(() -> new ProjectNotVisibleException(currentUser, project.getKey()));
    }
}
