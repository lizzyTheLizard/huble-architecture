package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.CreateProjectUseCase;
import site.gutschi.humble.spring.users.api.EditProjectUseCase;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.api.ViewProjectsUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService implements CreateProjectUseCase, EditProjectUseCase, ViewProjectsUseCase {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserInformation currentUserInformation;
    private final CanAccessProjectPolicy canAccessProjectPolicy;
    private final CanAccessUserPolicy canAccessUserPolicy;
    private final KeyUniquePolicy keyUniquePolicy;
    private final ProjectValidPolicy projectValidPolicy;
    private final GetProjectApi getProjectApi;


    @Override
    public GetProjectResponse getProject(String projectKey) {
        final var project = getProjectApi.getProject(projectKey);
        return new GetProjectResponse(project, canAccessProjectPolicy.canManage(project));
    }

    @Override
    public Set<Project> getAllProjects() {
        return getProjectApi.getAllProjects();
    }

    @Override
    public Project createProject(CreateProjectRequest request) {
        keyUniquePolicy.ensureProjectKeyUnique(request.key());
        canAccessProjectPolicy.ensureCanCreate();
        final var currentUser = currentUserInformation.getCurrentUser();
        final var project = Project.createNew(request.key(), request.name(), currentUser);
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
        final var project = getProjectApi.getProject(request.projectKey());
        final var currentUser = currentUserInformation.getCurrentUser();
        canAccessProjectPolicy.ensureCanManage(project);
        project.setName(request.name(), currentUser);
        project.setActive(request.active(), currentUser);
        project.setEstimations(request.estimations());
        projectValidPolicy.ensureProjectValid(project);
        projectRepository.save(project);
        log.info("Project {} edited", project.getKey());
    }

    @Override
    public void assignUser(AssignUserRequest request) {
        final var project = getProjectApi.getProject(request.projectKey());
        final var currentUser = currentUserInformation.getCurrentUser();
        canAccessProjectPolicy.ensureCanManage(project);
        final var user = getUserInternal(request.userEmail());
        project.setUserRole(user, request.type(), currentUser);
        projectRepository.save(project);
        log.info("User {} assigned to project {}", user.getEmail(), project.getKey());
    }

    @Override
    public void unAssignUser(UnAssignUserRequest request) {
        final var project = getProjectApi.getProject(request.projectKey());
        final var currentUser = currentUserInformation.getCurrentUser();
        canAccessProjectPolicy.ensureCanManage(project);
        final var user = getUserInternal(request.userEmail());
        project.removeUserRole(user, currentUser);
        projectRepository.save(project);
        log.info("User {} unassigned from project {}", user.getEmail(), project.getKey());
    }

    private User getUserInternal(String userEmail) {
        return userRepository.findByMail(userEmail)
                .orElseThrow(() -> canAccessUserPolicy.userNotFound(userEmail));
    }
}
