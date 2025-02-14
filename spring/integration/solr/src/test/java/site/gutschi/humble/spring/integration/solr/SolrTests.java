package site.gutschi.humble.spring.integration.solr;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.api.FindTasksResponse;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCallerRequest;
import site.gutschi.humble.spring.users.model.Project;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class SolrTests {
    @Container
    static final SolrContainer container = new SolrContainer("solr");
    @Autowired
    private SolrCaller target;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("solr.url", () -> String.format("http://%s:%d/solr/%s",
                container.getHost(),
                container.getMappedPort(SolrContainer.SOLR_PORT),
                SolrContainer.COLLECTION_NAME));
    }

    @Test
    void emptyIndex() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var request = new SearchCallerRequest("test", 1, 10, List.of(project));
        target.clear();

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void findTaskByKey() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task = createTask(1);
        final var request = new SearchCallerRequest("PRO-1", 1, 10, List.of(project));
        target.clear();
        target.informUpdatedTasks(task);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.tasks()).containsExactly(toView(task));
    }

    @Test
    void delete() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task = createTask(1);
        final var request = new SearchCallerRequest("PRO-1", 1, 10, List.of(project));
        target.informUpdatedTasks(task);
        target.informDeletedTasks(task);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void clear() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task = createTask(1);
        final var request = new SearchCallerRequest("PRO-1", 1, 10, List.of(project));
        target.informUpdatedTasks(task);
        target.clear();

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void onlyAllowedProjects() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("OTHER");
        final var task = createTask(1);
        final var request = new SearchCallerRequest("PRO-1", 1, 10, List.of(project));
        target.clear();
        target.informUpdatedTasks(task);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(0);
        assertThat(response.tasks()).isEmpty();
    }

    @Test
    void findTaskByDescription() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task1 = createTask(1);
        final var task2 = createTask(2);
        final var request = new SearchCallerRequest("STR2", 1, 10, List.of(project));
        target.clear();
        target.informUpdatedTasks(task1, task2);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.tasks()).containsExactly(toView(task2));
    }

    @Test
    void findTaskByDescriptionAndTitle() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task1 = createTask(1);
        final var task2 = createTask(2);
        final var request = new SearchCallerRequest("STR1", 1, 10, List.of(project));
        target.clear();
        target.informUpdatedTasks(task1, task2);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(2);
        assertThat(response.tasks()).containsExactly(toView(task2), toView(task1));
    }


    @Test
    void pagination() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        final var task1 = createTask(1);
        final var task2 = createTask(2);
        final var request = new SearchCallerRequest("STR1", 1, 1, List.of(project));
        final var request2 = new SearchCallerRequest("STR1", 2, 1, List.of(project));
        target.clear();
        target.informUpdatedTasks(task1, task2);

        final var response = target.findTasks(request);

        assertThat(response.total()).isEqualTo(2);
        assertThat(response.tasks()).containsExactly(toView(task2));

        final var response2 = target.findTasks(request2);

        assertThat(response2.total()).isEqualTo(2);
        assertThat(response2.tasks()).containsExactly(toView(task1));
    }


    private Task createTask(int i) {
        return Task.builder()
                .currentUserApi(Mockito.mock(CurrentUserApi.class))
                .creatorEmail("test@example.com")
                .status(TaskStatus.FUNNEL)
                .projectKey("PRO")
                .id(i)
                .description("A potential long description of the Task STR" + i)
                .title("The Title of the Task STR" + (i - 1))
                .build();

    }

    private FindTasksResponse.TaskFindView toView(Task task) {
        return new FindTasksResponse.TaskFindView(task.getKey(), task.getTitle(), task.getAssigneeEmail().orElse(null), task.getStatus());
    }
}
