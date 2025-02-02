package site.gutschi.humble.spring.tasks.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.domain.api.EditTaskUseCase;
import site.gutschi.humble.spring.tasks.domain.api.EditTaskRequest;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditTaskTest {
    @Autowired
    private EditTaskUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    @Test
    void editNotActive() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"), "assignee", 1);
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void editWithoutAccess() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  "assignee", 1);
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void editReadAccessOnly() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  "assignee", 1);
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void editDeleted() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(true);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  "assignee", 1);
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void editNotFound() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  "assignee", 1);
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.edit(request));
    }

    @Test
    void edit() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.getTitle()).thenReturn("title");
        Mockito.when(task.getDescription()).thenReturn("description");
        Mockito.when(task.getStatus()).thenReturn(TaskStatus.FUNNEL);
        Mockito.when(task.getAssigneeEmail()).thenReturn(Optional.empty());
        Mockito.when(task.getEstimation()).thenReturn(Optional.empty());
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  "assignee", 1);
        target.edit(request);

        Mockito.verify(task).setTitle("new title");
        Mockito.verify(task).setDescription("new description");
        Mockito.verify(task).setStatus(TaskStatus.TODO);
        Mockito.verify(task).setAssigneeEmail("assignee");
        Mockito.verify(task).setEstimation(1);
        Mockito.verify(task).setAdditionalFields(Map.of("key", "value"));
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void editUnset() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.getTitle()).thenReturn("title");
        Mockito.when(task.getDescription()).thenReturn("description");
        Mockito.when(task.getStatus()).thenReturn(TaskStatus.FUNNEL);
        Mockito.when(task.getAssigneeEmail()).thenReturn(Optional.of("asignee"));
        Mockito.when(task.getEstimation()).thenReturn(Optional.of(1));
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, Map.of("key", "value"),  null, null);
        target.edit(request);

        Mockito.verify(task).setTitle("new title");
        Mockito.verify(task).setDescription("new description");
        Mockito.verify(task).setStatus(TaskStatus.TODO);
        Mockito.verify(task).setAssigneeEmail(null);
        Mockito.verify(task).setEstimation(null);
        Mockito.verify(taskRepository).save(task);
    }
}