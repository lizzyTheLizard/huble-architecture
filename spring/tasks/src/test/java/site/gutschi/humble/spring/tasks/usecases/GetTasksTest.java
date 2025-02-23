package site.gutschi.humble.spring.tasks.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GetTasksTest {
    @Autowired
    private GetTasksUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    private Project testProject;
    private Task existingTask;

    @BeforeEach
    void setup() {
        User currentUser = new User("dev@example.com", "Hans");
        testProject = Project.createNew("PRO", "Test", currentUser, currentUserApi);
        existingTask = Task.createNew(currentUserApi, testProject.getKey(), 13, "Test", "Test");
        Mockito.when(taskRepository.findByProject(testProject)).thenReturn(Set.of(existingTask));
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(getProjectUseCase.getProject(testProject.getKey())).thenReturn(testProject);
    }

    @Test
    void getTasks() {
        final var result = target.getTasksForProject(testProject.getKey());

        assertThat(result).singleElement().isEqualTo(existingTask);
    }
}

