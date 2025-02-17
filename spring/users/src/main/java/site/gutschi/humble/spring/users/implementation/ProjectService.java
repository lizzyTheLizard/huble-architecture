package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.*;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService implements CreateProjectUseCase, EditProjectUseCase, GetProjectUseCase {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserApi currentUserApi;
    private final CanAccessProjectPolicy canAccessProjectPolicy;
    private final KeyUniquePolicy keyUniquePolicy;
    private final CanCreateProjectPolicy canCreateProjectPolicy;

    @Override
    public void assignUser(AssignUserRequest request) {
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        canAccessProjectPolicy.ensureCanManage(project);
        final var user = userRepository.findByMail(request.userEmail())
                .orElseThrow(() -> new UserNotFoundException(request.userEmail()));
        project.setUserRole(user, request.type());
        projectRepository.save(project);
        log.info("User {} assigned to project {}", user.getEmail(), project.getKey());
    }

    @Override
    public void unAssignUser(UnAssignUserRequest request) {
        final var user = userRepository.findByMail(request.userEmail())
                .orElseThrow(() -> new UserNotFoundException(request.userEmail()));
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        canAccessProjectPolicy.ensureCanManage(project);
        project.removeUserRole(user);
        projectRepository.save(project);
        log.info("User {} unassigned from project {}", user.getEmail(), project.getKey());
    }

    @Override
    public Project createProject(CreateProjectRequest request) {
        final var currentUser = userRepository.findByMail(currentUserApi.currentEmail())
                .orElseThrow(() -> new UserNotFoundException(currentUserApi.currentEmail()));
        keyUniquePolicy.ensureProjectKeyUnique(request.key());
        canCreateProjectPolicy.ensureCanCreateProject();
        final var project = Project.createNew(request.key(), request.name(), currentUser);
        projectRepository.save(project);
        log.info("Project {} created", project.getKey());
        return project;
    }

    @Override
    public boolean canCreateProject() {
        return currentUserApi.isSystemAdmin();
    }

    @Override
    public void editProject(EditProjectRequest request) {
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        canAccessProjectPolicy.ensureCanManage(project);
        project.setName(request.name());
        project.setActive(request.active());
        project.setEstimations(request.estimations());
        projectRepository.save(project);
        log.info("Project {} edited", project.getKey());
    }

    @Override
    public GetProjectResponse getProject(String projectKey) {
        final var project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new ProjectNotFoundException(projectKey));
        canAccessProjectPolicy.ensureCanRead(project);
        return new GetProjectResponse(project, canAccessProjectPolicy.canManage(project));
    }

    @Override
    public Collection<Project> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(canAccessProjectPolicy::canRead)
                .toList();
    }
}
