package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class GetProjectService implements GetProjectUseCase {
    private final ProjectRepository projectRepository;
    private final CanAccessProjectPolicy canAccessProjectPolicy;

    @Override
    public Project getProject(String projectKey) {
        final var project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> canAccessProjectPolicy.projectNotFound(projectKey));
        canAccessProjectPolicy.ensureCanRead(project);
        return project;
    }

    @Override
    public Set<Project> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(canAccessProjectPolicy::canRead)
                .collect(Collectors.toSet());
    }
}
