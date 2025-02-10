package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.*;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService implements CreateProjectUseCase, EditProjectUseCase, GetProjectUseCase {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserApi currentUserApi;
    private final AllowedToAccessPolicy allowedToAccessPolicy;
    private final KeyUniquePolicy keyUniquePolicy;

    @Override
    public void assignUser(AssignUserRequest request) {
        final var user = userRepository.findByMail(request.userEmail())
                .orElseThrow(() -> new UserNotFoundException(request.userEmail()));
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        allowedToAccessPolicy.ensureCanManage(project);
        project.setUserRole(user, request.type());
        projectRepository.save(project);
        log.info("User {} assigned to project {}", user.getEmail(), project.getKey());
    }

    @Override
    public Project createProject(CreateProjectRequest request) {
        final var currentUser = userRepository.findByMail(currentUserApi.currentEmail())
                .orElseThrow(() -> new UserNotFoundException(currentUserApi.currentEmail()));
        final var initialAdminRole = new ProjectRole(currentUser, ProjectRoleType.ADMIN);
        keyUniquePolicy.ensureProjectKeyUnique(request.key());
        final var project = Project.builder()
                .key(request.key())
                .name(request.name())
                .projectRole(initialAdminRole)
                .build();
        projectRepository.save(project);
        log.info("Project {} created", project.getKey());
        return project;
    }

    @Override
    public void editProject(EditProjectRequest request) {
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        allowedToAccessPolicy.ensureCanManage(project);
        project.setName(request.name());
        project.setActive(request.active());
        projectRepository.save(project);
        log.info("Project {} edited", project.getKey());
    }

    @Override
    public Optional<Project> getProject(String projectKey) {
        return projectRepository.findByKey(projectKey)
                .filter(allowedToAccessPolicy::canRead);
    }

    @Override
    public Collection<Project> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(allowedToAccessPolicy::canRead)
                .toList();
    }
}
