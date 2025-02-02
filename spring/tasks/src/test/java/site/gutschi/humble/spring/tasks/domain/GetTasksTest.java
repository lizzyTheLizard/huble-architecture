package site.gutschi.humble.spring.tasks.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.GetTasksRequest;
import site.gutschi.humble.spring.tasks.domain.api.GetTasksUseCase;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class GetTasksTest {
    @Autowired
    private GetTasksUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;


    @Test
    void getNonExistingTask() {
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getTaskByKey("PRO-12"));
    }

    @Test
    void getWithoutAccess() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getTaskByKey("PRO-12"));
    }

    @Test
    void getTask() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.getTaskByKey("PRO-12");

        assertThat(result).isEqualTo(task);
    }

    @Test
    void findTasksFilterNotAllowed() {
        final var request = new GetTasksRequest("PRO", null, null, null, null, 0, 10);
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findTasks(request)).thenReturn(List.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.getTasks(request);

        assertThat(result).isEmpty();
    }


    @Test
    void findTasks() {
        final var request = new GetTasksRequest("PRO", null, null, null, null, 0, 10);
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findTasks(request)).thenReturn(List.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.getTasks(request);

        assertThat(result).containsExactly(task);
    }

    @Test
    void countTasksFilterNotAllowed() {
        final var request = new GetTasksRequest("PRO", null, null, null, null, 0, 10);
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findTasksWithoutPaging(request)).thenReturn(List.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.count(request);

        assertThat(result).isEqualTo(0);
    }


    @Test
    void count() {
        final var request = new GetTasksRequest("PRO", null, null, null, null, 0, 10);
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findTasksWithoutPaging(request)).thenReturn(List.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.count(request);

        assertThat(result).isEqualTo(1);
    }
}
