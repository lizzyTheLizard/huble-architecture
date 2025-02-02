package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;

@Service
@RequiredArgsConstructor
public class ProjectActivePolicy {
    public void ensureProjectIsActive(Project project) {
        if (!project.isActive()) {
            throw new NotAllowedException("You are not allowed to access archived project '" + project.getKey() + "'");
        }
    }
}
