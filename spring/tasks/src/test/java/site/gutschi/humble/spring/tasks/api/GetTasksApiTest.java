package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GetTasksApiTest {
    @Autowired
    private GetTasksApi target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    private Project testProject;
    private Task existingTask;

    @BeforeEach
    void setup() {
        final var currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test", currentUser);
        existingTask = Task.createNew(testProject, 13, "Test", "Test", currentUser);
        Mockito.when(taskRepository.findByProject(testProject)).thenReturn(Set.of(existingTask));
        Mockito.when(currentUserApi.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(getProjectApi.getProject(testProject.getKey())).thenReturn(testProject);
    }

    @Test
    void getTasks() {
        final var result = target.getTasksForProject(testProject);

        assertThat(result).singleElement().isEqualTo(existingTask);
    }
}

