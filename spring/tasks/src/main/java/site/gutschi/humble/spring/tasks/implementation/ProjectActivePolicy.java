package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;

@Service
@RequiredArgsConstructor
public class ProjectActivePolicy {
    public void ensureProjectIsActive(Project project) {
        if (!project.isActive()) {
            throw NotAllowedException.projectNotActive(project.getKey());
        }
    }
}
