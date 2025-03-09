package site.gutschi.humble.spring.integration.solr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.test.SolrContainer;
import site.gutschi.humble.spring.tasks.api.ViewTasksUseCase;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class SolrTests {
    @Container
    static final SolrContainer container = new SolrContainer().withConfigDir("solr");
    @Autowired
    private SolrCaller target;

    private User currentUser;
    private Project testProject;
    private SearchCaller.SearchCallerRequest request;
    private Task task1;
    private Task task2;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test Project", currentUser);
        target.clear();
        task1 = Task.builder()
                .creator(currentUser)
                .status(TaskStatus.FUNNEL)
                .project(testProject)
                .id(1)
                .description("A potential long description of the Task with special String STR1")
                .title("The Title of the Task without special string")
                .build();
        task2 = Task.builder()
                .creator(currentUser)
                .status(TaskStatus.FUNNEL)
                .project(testProject)
                .id(2)
                .description("A potential long description of the Task with special String STR2")
                .title("The Title of the Task with special string STR1")
                .build();
        target.informUpdatedTasks(task1, task2);
        request = new SearchCaller.SearchCallerRequest(task1.getKey().toString(), 1, 10, List.of(testProject));
    }

    private ViewTasksUseCase.TaskFindView toView(Task task) {
        return new ViewTasksUseCase.TaskFindView(task.getKey(), task.getTitle(), task.getAssignee().map(User::getEmail).orElse(null), task.getStatus());
    }

    @Test
    void findExistingTask() {
        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.tasks()).containsExactly(toView(task1));
    }

    @Test
    void deleted() {
        target.informDeletedTasks(task1);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void cleared() {
        target.clear();

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void onlyAllowedProjects() {
        final var otherProject = Project.createNew("OTHER", "Test Project", currentUser);
        final var request = new SearchCaller.SearchCallerRequest(task1.getKey().toString(), 1, 10, List.of(otherProject));

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void findByDescription() {
        final var request = new SearchCaller.SearchCallerRequest("STR2", 1, 10, List.of(testProject));

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.tasks()).containsExactly(toView(task2));
    }

    @Test
    void findByDescriptionAndTitle() {
        final var request = new SearchCaller.SearchCallerRequest("STR1", 1, 10, List.of(testProject));

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(2);
        assertThat(response.tasks()).containsExactly(toView(task2), toView(task1));
    }

    @Test
    void pagination() {
        final var request = new SearchCaller.SearchCallerRequest("STR1", 1, 1, List.of(testProject));
        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(2);
        assertThat(response.tasks()).containsExactly(toView(task2));

        final var request2 = new SearchCaller.SearchCallerRequest("STR1", 2, 1, List.of(testProject));
        final var response2 = target.findTasks(request2);

        assertThat(response2.total()).isEqualTo(2);
        assertThat(response2.tasks()).containsExactly(toView(task1));
    }
}