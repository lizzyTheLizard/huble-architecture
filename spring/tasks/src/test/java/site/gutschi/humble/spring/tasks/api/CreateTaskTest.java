package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.TestApplication;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.api.ProjectNotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class CreateTaskTest {
    @Autowired
    private CreateTaskUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @Test
    void createTaskNonExistingProject() {
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.empty());

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(ProjectNotFoundException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskReadAccessOnly() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskProjectNotActive() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTask() {
        Mockito.when(taskRepository.nextId("PRO")).thenReturn(42);
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

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
        assertThat(createdTask.getHistoryEntries()).isEmpty();
    }

    @Test
    void getProjectsToCreate() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(List.of(project));

        final var response = target.getProjectsToCreate();

        assertThat(response).singleElement().isEqualTo(project);
    }

}