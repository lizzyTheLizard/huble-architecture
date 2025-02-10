package site.gutschi.humble.spring.integration.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
public class InMemoryInitializer {
    private final TimeApi timeApi;
    private final CurrentUserApi currentUserApi;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SearchCaller searchCaller;

    @PostConstruct
    public void init() {
        final var user1 = User.builder()
                .name("Test Manager")
                .email("test@example.com")
                .password("{noop}password")
                .systemAdmin(false)
                .build();
        userRepository.save(user1);
        final var user2 = User.builder()
                .name("Test Developer")
                .email("dev@example.com")
                .password("{noop}password")
                .systemAdmin(false)
                .build();
        userRepository.save(user2);
        final var user3 = User.builder()
                .name("Test Extern")
                .email("extern@example.com")
                .password("{noop}password")
                .systemAdmin(false)
                .build();
        userRepository.save(user3);
        final var user4 = User.builder()
                .name("Sys Admin")
                .email("admin@example.com")
                .password("{noop}password")
                .systemAdmin(true)
                .build();
        userRepository.save(user4);
        final var project = Project.builder()
                .active(true)
                .key("PRO")
                .name("Project PRO")
                .projectRole(new ProjectRole(user1, ProjectRoleType.ADMIN))
                .projectRole(new ProjectRole(user2, ProjectRoleType.DEVELOPER))
                .currentUserApi(currentUserApi)
                .timeApi(timeApi)
                .build();
        projectRepository.save(project);
        searchCaller.clear();
        for (var i = 1; i < 20; i++) {
            final var task = Task.builder()
                    .id(taskRepository.nextId("PRO"))
                    .creatorEmail(user1.getEmail())
                    .comment(new Comment("test@example.com", timeApi.now(), "This is a comment"))
                    .description("This is the description")
                    .projectKey("PRO")
                    .status(TaskStatus.FUNNEL)
                    .estimation(3)
                    .assigneeEmail(user2.getEmail())
                    .timeApi(timeApi)
                    .currentUserApi(currentUserApi)
                    .title("Title of PRO-" + i)
                    .build();
            taskRepository.save(task);
            searchCaller.informUpdatedTasks(task);
        }
    }
}
