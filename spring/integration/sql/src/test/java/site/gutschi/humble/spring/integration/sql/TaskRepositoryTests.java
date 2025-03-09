package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.common.test.PostgresContainer;
import site.gutschi.humble.spring.tasks.model.*;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class TaskRepositoryTests {
    final static User u1 = User.builder()
            .email("u1@example.com")
            .build();
    final static Project p1 = Project.builder()
            .key("p1")
            .name("Project 1")
            .active(true)
            .build();
    @Container
    static final PostgresContainer container = new PostgresContainer();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    private static Stream<Task> provideTasks() {
        TimeHelper.setNow(Instant.ofEpochMilli(1000));
        final var taskBuilder = Task.builder()
                .project(p1)
                .creator(u1)
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.BACKLOG)
                .deleted(false)
                .comment(new Comment(u1, TimeHelper.now(), "Comment 1"))
                .historyEntry(new TaskHistoryEntry(u1, TimeHelper.now(), TaskHistoryType.CREATED, null, null, null));
        return Stream.of(
                taskBuilder.id(1).build(),
                taskBuilder.id(2).title("Task 2").build(),
                taskBuilder.id(3).estimation(3).build(),
                taskBuilder.id(4).deleted(true).build(),
                taskBuilder.id(5).assignee(u1).build(),
                taskBuilder.id(6).comment(new Comment(u1, TimeHelper.now(), "Comment 2")).build(),
                taskBuilder.id(7).historyEntry(new TaskHistoryEntry(u1, TimeHelper.now(), TaskHistoryType.EDITED, "Status", "BACKLOG", "IN_PROGRESS")).build()
        );
    }

    @BeforeEach
    void setup() {
        userRepository.save(u1);
        projectRepository.save(p1);
    }

    @ParameterizedTest
    @MethodSource("provideTasks")
    void saveAndReload(Task task) {
        taskRepository.save(task);

        final var result = taskRepository.findByKey(task.getKey().toString());
        assertThat(result).isPresent();
        assertThat(result.get().getKey()).isEqualTo(task.getKey());
        assertThat(result.get().getCreator()).isEqualTo(task.getCreator());
        assertThat(result.get().getStatus()).isEqualTo(task.getStatus());
        assertThat(result.get().getTitle()).isEqualTo(task.getTitle());
        assertThat(result.get().getDescription()).isEqualTo(task.getDescription());
        assertThat(result.get().isDeleted()).isEqualTo(task.isDeleted());
        assertThat(result.get().getEstimation()).isEqualTo(task.getEstimation());
        assertThat(result.get().getAssignee()).isEqualTo(task.getAssignee());
        assertThat(result.get().getComments()).hasSize(task.getComments().size());
        result.get().getComments().forEach(resultComment -> {
            final var taskComment = task.getComments().stream().filter(c -> c.text().equals(resultComment.text())).findFirst();
            assertThat(taskComment).isPresent();
            assertThat(resultComment.user()).isEqualTo(taskComment.get().user());
            assertThat(resultComment.text()).isEqualTo(taskComment.get().text());
            assertThat(resultComment.timestamp()).isEqualTo(taskComment.get().timestamp());
        });
        assertThat(result.get().getHistoryEntries()).hasSize(task.getHistoryEntries().size());
        result.get().getHistoryEntries().forEach(resultEntry -> {
            final var taskEntry = task.getHistoryEntries().stream().filter(e -> e.description().equals(resultEntry.description())).findFirst();
            assertThat(taskEntry).isPresent();
            assertThat(resultEntry.user()).isEqualTo(taskEntry.get().user());
            assertThat(resultEntry.timestamp()).isEqualTo(taskEntry.get().timestamp());
            assertThat(resultEntry.type()).isEqualTo(taskEntry.get().type());
            assertThat(resultEntry.field()).isEqualTo(taskEntry.get().field());
            assertThat(resultEntry.oldValue()).isEqualTo(taskEntry.get().oldValue());
            assertThat(resultEntry.newValue()).isEqualTo(taskEntry.get().newValue());
        });
    }

    @Test
    void notFound() {
        final var result = taskRepository.findByKey("WRONG-1");
        assertThat(result).isEmpty();
    }

    @Test
    void nextId() {
        final var old = taskRepository.nextId(p1);

        final var result = taskRepository.nextId(p1);

        assertThat(result).isEqualTo(old + 1);
    }

    @Test
    void findByProject() {
        final var p2 = Project.builder()
                .key("FFU2")
                .name("Find All 2")
                .projectRole(new ProjectRole(u1, ProjectRoleType.STAKEHOLDER))
                .build();
        projectRepository.save(p2);
        final var task = Task.builder()
                .project(p2)
                .creator(u1)
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.BACKLOG)
                .deleted(false)
                .comment(new Comment(u1, TimeHelper.now(), "Comment 1"))
                .historyEntry(new TaskHistoryEntry(u1, TimeHelper.now(), TaskHistoryType.CREATED, null, null, null))
                .build();
        taskRepository.save(task);

        final var result = taskRepository.findByProject(p2);
        assertThat(result).allMatch(t -> t.getKey().equals(task.getKey()));

        final var result2 = taskRepository.findByProject(p1);
        assertThat(result2).noneMatch(t -> t.getKey().equals(task.getKey()));
    }
}
