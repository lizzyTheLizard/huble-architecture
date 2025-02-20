package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.CreateProjectUseCase;
import site.gutschi.humble.spring.users.api.EditProjectUseCase;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService implements CreateProjectUseCase, EditProjectUseCase, GetProjectUseCase {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserApi currentUserApi;
    private final CanAccessProjectPolicy canAccessProjectPolicy;
    private final CanAccessUserPolicy canAccessUserPolicy;
    private final KeyUniquePolicy keyUniquePolicy;
    private final ProjectValidPolicy projectValidPolicy;


    @Override
    public GetProjectResponse getProject(String projectKey) {
        final var project = getProjectInternal(projectKey);
        return new GetProjectResponse(project, canAccessProjectPolicy.canManage(project));
    }

    @Override
    public Set<Project> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(canAccessProjectPolicy::canRead)
                .collect(Collectors.toSet());
    }

    @Override
    public Project createProject(CreateProjectRequest request) {
        keyUniquePolicy.ensureProjectKeyUnique(request.key());
        canAccessProjectPolicy.ensureCanCreate();
        final var currentUser = getUserInternal(currentUserApi.currentEmail());
        final var project = Project.createNew(request.key(), request.name(), currentUser, currentUserApi);
        projectValidPolicy.ensureProjectValid(project);
        projectRepository.save(project);
        log.info("Project {} created", project.getKey());
        return project;
    }

    @Override
    public boolean canCreateProject() {
        return canAccessProjectPolicy.canCreate();
    }

    @Override
    public void editProject(EditProjectRequest request) {
        final var project = getProjectInternal(request.projectKey());
        canAccessProjectPolicy.ensureCanManage(project);
        project.setName(request.name());
        project.setActive(request.active());
        project.setEstimations(request.estimations());
        projectValidPolicy.ensureProjectValid(project);
        projectRepository.save(project);
        log.info("Project {} edited", project.getKey());
    }

    @Override
    public void assignUser(AssignUserRequest request) {
        final var project = getProjectInternal(request.projectKey());
        canAccessProjectPolicy.ensureCanManage(project);
        final var user = getUserInternal(request.userEmail());
        project.setUserRole(user, request.type());
        projectRepository.save(project);
        log.info("User {} assigned to project {}", user.getEmail(), project.getKey());
    }

    @Override
    public void unAssignUser(UnAssignUserRequest request) {
        final var project = getProjectInternal(request.projectKey());
        canAccessProjectPolicy.ensureCanManage(project);
        final var user = getUserInternal(request.userEmail());
        project.removeUserRole(user);
        projectRepository.save(project);
        log.info("User {} unassigned from project {}", user.getEmail(), project.getKey());
    }

    private Project getProjectInternal(String projectKey) {
        final var project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> canAccessProjectPolicy.projectNotFound(projectKey));
        canAccessProjectPolicy.ensureCanRead(project);
        return project;
    }

    private User getUserInternal(String userEmail) {
        return userRepository.findByMail(userEmail)
                .orElseThrow(() -> canAccessUserPolicy.userNotFound(userEmail));
    }
}
