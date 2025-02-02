package site.gutschi.humble.spring.users.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.users.domain.api.NotAllowedException;
import site.gutschi.humble.spring.users.domain.api.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.domain.ports.UserRepository;

@RequiredArgsConstructor
@Service
public class AllowedToAccessPolicy {
    private final UserApi userApi;
    private final UserRepository userRepository;

    public void ensureCanManage(Project project) {
        final var currentUser = getCurrentUser();
        if(currentUser.isSystemAdmin()) return;
        final var projectRoleType = getProjectRoleType(currentUser, project);
        if(!projectRoleType.canManage()) {
            throw NotAllowedException.notAllowedToManageProject(currentUser.getEmail(), project.getKey());
        }
    }

    public void ensureCanEdit(User user) {
        final var currentUser = getCurrentUser();
        if(user.isSystemAdmin()) return;
        if(!currentUser.getEmail().equals(user.getEmail())) {
            throw NotAllowedException.notAllowedToManageUser(currentUser.getEmail(), user.getEmail());
        }
    }

    public boolean canRead(Project project) {
        final var currentUser = getCurrentUser();
        if(currentUser.isSystemAdmin()) return true;
        return project.getRole(currentUser.getEmail())
                .map(ProjectRoleType::canRead)
                .orElse(false);
    }

    private User getCurrentUser(){
        final var currentEmail = userApi.currentEmail();
        return userRepository.findByMail(currentEmail)
                .orElseThrow(() -> NotFoundException.userNotFound(currentEmail));
    }

    private ProjectRoleType getProjectRoleType(User user, Project project){
        return project.getRole(user.getEmail())
                .orElseThrow(() -> NotFoundException.projectNotVisible(user.getEmail(), project.getKey()));
    }
}
