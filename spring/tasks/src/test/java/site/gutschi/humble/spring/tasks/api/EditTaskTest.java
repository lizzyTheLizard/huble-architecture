package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.TestApplication;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditTaskTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private EditTaskUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @Test
    void editNotActive() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.edit(request));

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
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.edit(request));

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
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void editNotFound() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);
        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> target.edit(request));
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
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);
        target.edit(request);

        Mockito.verify(task).setTitle("new title");
        Mockito.verify(task).setDescription("new description");
        Mockito.verify(task).setStatus(TaskStatus.TODO);
        Mockito.verify(task).setAssigneeEmail("assigneeEmail");
        Mockito.verify(task).setEstimation(1);
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void editUnset() {
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.getTitle()).thenReturn("title");
        Mockito.when(task.getDescription()).thenReturn("description");
        Mockito.when(task.getStatus()).thenReturn(TaskStatus.FUNNEL);
        Mockito.when(task.getAssigneeEmail()).thenReturn(Optional.of("assigneeEmail"));
        Mockito.when(task.getEstimation()).thenReturn(Optional.of(1));
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));

        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, null, null);
        target.edit(request);

        Mockito.verify(task).setTitle("new title");
        Mockito.verify(task).setDescription("new description");
        Mockito.verify(task).setStatus(TaskStatus.TODO);
        Mockito.verify(task).setAssigneeEmail(null);
        Mockito.verify(task).setEstimation(null);
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void deleteNotActive() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new DeleteTaskRequest("PRO-13");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.delete(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteReadAccessOnly() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new DeleteTaskRequest("PRO-13");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.delete(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteDeleted() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(true);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new DeleteTaskRequest("PRO-13");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.delete(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void deleteNotFound() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        final var request = new DeleteTaskRequest("PRO-13");
        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> target.delete(request));
    }

    @Test
    void delete() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.ADMIN));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new DeleteTaskRequest("PRO-13");
        target.delete(request);

        Mockito.verify(task).setDeleted();
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void commentNotActive() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void commentDeleted() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(true);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void commentNotFound() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void comment() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        target.comment(request);

        Mockito.verify(task).addComment("new comment");
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void commentReadAccessOnly() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        target.comment(request);

        Mockito.verify(task).addComment("new comment");
        Mockito.verify(taskRepository).save(task);
    }
}