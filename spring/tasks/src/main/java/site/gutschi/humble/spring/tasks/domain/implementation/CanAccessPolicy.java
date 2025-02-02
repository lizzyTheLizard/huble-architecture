package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

@Service
@RequiredArgsConstructor
public class CanAccessPolicy {
    private final UserApi userApi;

    public void ensureReadAccess(Project project) {
        if(!canRead(project)) {
            throw NotFoundException.projectNotFound(project.getKey());
        }
    }

    public boolean canRead(Project project) {
        if(userApi.isSystemAdmin()) return true;
        final var currentEmail = userApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canRead)
                .orElse(false);
    }


    public void ensureDeleteAccess(Project project) {
        if(!canRead(project)) {
            throw NotFoundException.projectNotFound(project.getKey());
        }
        if(!canDelete(project)) {
            throw new NotAllowedException("You are not allowed to delete tasks in project '" + project.getKey() + "'");
        }
    }

    public boolean canDelete(Project project) {
        if(userApi.isSystemAdmin()) return true;
        final var currentEmail = userApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canManage)
                .orElse(false);
    }

    public void ensureWriteAccess(Project project) {
        if(!canRead(project)) {
            throw NotFoundException.projectNotFound(project.getKey());
        }
        if(!canWrite(project)) {
            throw new NotAllowedException("You are not allowed to write project '" + project.getKey() + "'");
        }
    }

    public boolean canWrite(Project project) {
        if(userApi.isSystemAdmin()) return true;
        final var currentEmail = userApi.currentEmail();
        return project.getRole(currentEmail)
                .map(ProjectRoleType::canWrite)
                .orElse(false);
    }

}


