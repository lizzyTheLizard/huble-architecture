package site.gutschi.humble.spring.tasks.implementation;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.tasks.api.EditTaskNotAllowedException;
import site.gutschi.humble.spring.users.model.Project;

@Service
public class ProjectActivePolicy {
    public void ensureProjectIsActive(Project project) {
        if (!project.isActive()) {
            throw new EditTaskNotAllowedException(project.getKey());
        }
    }
}
