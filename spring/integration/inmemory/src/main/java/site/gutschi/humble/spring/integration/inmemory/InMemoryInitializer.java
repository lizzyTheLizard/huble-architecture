package site.gutschi.humble.spring.integration.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.helper.TimeHelper;
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

import java.util.List;
import java.util.stream.IntStream;

// TODO Configuration: Make use of immemory database configurable
@Service
@RequiredArgsConstructor
public class InMemoryInitializer {
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
                .build();
        final var user2 = User.builder()
                .name("Test Developer")
                .email("dev@example.com")
                .build();
        final var user3 = User.builder()
                .name("Test Extern")
                .email("extern@example.com")
                .build();
        final var user4 = User.builder()
                .name("Sys Admin")
                .email("admin@example.com")
                .build();
        final var project = Project.builder()
                .active(true)
                .key("PRO")
                .name("Project PRO")
                .projectRole(new ProjectRole(user1, ProjectRoleType.ADMIN))
                .projectRole(new ProjectRole(user2, ProjectRoleType.DEVELOPER))
                .estimation(1)
                .estimation(3)
                .estimation(5)
                .historyEntries(List.of())
                .currentUserApi(currentUserApi)
                .build();
        final var tasks = IntStream.range(1, 20)
                .mapToObj(i -> createTask(user1, user2, i))
                .toArray(Task[]::new);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        projectRepository.save(project);
        for (Task task : tasks) taskRepository.save(task);

        searchCaller.clear();
        searchCaller.informUpdatedTasks(tasks);
    }

    private Task createTask(User user1, User user2, int i) {
        return Task.builder()
                .id(taskRepository.nextId("PRO"))
                .creatorEmail(user1.getEmail())
                .comment(new Comment("test@example.com", TimeHelper.now(), "This is a comment"))
                .description("This is the description")
                .projectKey("PRO")
                .status(TaskStatus.FUNNEL)
                .estimation(3)
                .assigneeEmail(user2.getEmail())
                .currentUserApi(currentUserApi)
                .title("Title of PRO-" + i)
                .build();
    }
}
