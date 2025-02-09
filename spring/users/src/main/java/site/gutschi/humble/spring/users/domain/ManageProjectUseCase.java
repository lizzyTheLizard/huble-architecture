package site.gutschi.humble.spring.users.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.users.domain.api.*;
import site.gutschi.humble.spring.users.domain.ports.ProjectRepository;
import site.gutschi.humble.spring.users.domain.ports.UserRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ManageProjectUseCase implements CreateProjectApi, EditProjectApi, AssignUserApi, GetProjectApi {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserApi userApi;
    private final AllowedToAccessPolicy allowedToAccessPolicy;

    @Override
    public void assignUser(AssignUserRequest request) {
        final var user = userRepository.findByMail(request.userEmail())
                .orElseThrow(() -> NotFoundException.userNotFound(request.userEmail()));
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(request.projectKey()));
        allowedToAccessPolicy.ensureCanManage(project);
        project.setUserRole(user, request.type());
        projectRepository.save(project);
    }

    @Override
    public Project createProject(CreateProjectRequest request) {
        final var currentUser = userRepository.findByMail(userApi.currentEmail())
                .orElseThrow(() -> NotFoundException.userNotFound(userApi.currentEmail()));
        final var initialAdminRole = new ProjectRole(currentUser, ProjectRoleType.ADMIN);
        final var exising = projectRepository.findByKey(request.key());
        if (exising.isPresent())
            throw NotUniqueException.keyAlreadyExists(request.key());
        final var project = Project.builder()
                .key(request.key())
                .name(request.name())
                .projectRole(initialAdminRole)
                .build();
        projectRepository.save(project);
        return project;
    }

    @Override
    public void editProject(EditProjectRequest request) {
        final var project = projectRepository.findByKey(request.projectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(request.projectKey()));
        allowedToAccessPolicy.ensureCanManage(project);
        project.setName(request.name());
        project.setActive(request.active());
        projectRepository.save(project);
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
