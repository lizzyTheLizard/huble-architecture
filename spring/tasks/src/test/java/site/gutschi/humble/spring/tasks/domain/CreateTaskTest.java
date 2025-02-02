package site.gutschi.humble.spring.tasks.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.CreateTaskRequest;
import site.gutschi.humble.spring.tasks.domain.api.CreateTaskUseCase;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class CreateTaskTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private CreateTaskUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    @Test
    void createTaskNonExistingProject() {
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.empty());

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskWithoutAccess() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskReadAccessOnly() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskProjectNotActive() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTask() {
        Mockito.when(taskRepository.nextId("PRO")).thenReturn(42);
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        final var createdTask = target.create(request);

        Mockito.verify(taskRepository).save(createdTask);
        assertThat(createdTask.getCreatorEmail()).isEqualTo(TestApplication.CURRENT_USER.getEmail());
        assertThat(createdTask.getKey()).isEqualTo("PRO-42");
        assertThat(createdTask.getProjectKey()).isEqualTo("PRO");
        assertThat(createdTask.getTitle()).isEqualTo("title");
        assertThat(createdTask.getDescription()).isEqualTo("description");
        assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.FUNNEL);
        assertThat(createdTask.getAssigneeEmail()).isEmpty();
        assertThat(createdTask.getEstimation()).isEmpty();
        assertThat(createdTask.getComments()).isEmpty();
        assertThat(createdTask.getImplementations()).isEmpty();
        assertThat(createdTask.getFields()).isEmpty();
        assertThat(createdTask.getHistoryEntries()).isEmpty();
    }
}