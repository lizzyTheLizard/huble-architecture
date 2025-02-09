package site.gutschi.humble.spring.users.integration.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.users.domain.ports.ProjectRepository;
import site.gutschi.humble.spring.users.domain.ports.UserRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//TODO: Replace with a real database
public class ProjectRepositoryImplementation implements ProjectRepository {
    private final UserApi userApi;
    private final TimeApi timeApi;
    private final UserRepository userRepository;

    @Override
    public void save(Project project) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Optional<Project> findByKey(String key) {
        if (key.equals("PRO")) {
            final var user = userRepository.findByMail("test@example.com").orElseThrow();
            final var project = Project.builder()
                    .active(true)
                    .key("PRO")
                    .name("Project PRO")
                    .projectRole(new ProjectRole(user, ProjectRoleType.ADMIN))
                    .userApi(userApi)
                    .timeApi(timeApi)
                    .build();
            return Optional.of(project);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Project> findAll() {
        final var user = userRepository.findByMail("test@example.com").orElseThrow();
        final var project = Project.builder()
                .active(true)
                .key("PRO")
                .name("Project PRO")
                .projectRole(new ProjectRole(user, ProjectRoleType.ADMIN))
                .userApi(userApi)
                .timeApi(timeApi)
                .build();
        return List.of(project);
    }
}
