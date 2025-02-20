package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditTaskTest {
    @Autowired
    private EditTaskUseCase target;

    @Mock
    private Task existingTask;

    @Mock
    private User currentUser;

    @Mock
    private Project testProject;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @MockitoBean
    @SuppressWarnings("unused") // Used indirectly
    private SearchCaller searchCaller;

    @BeforeEach
    void setup() {
        Mockito.when(currentUser.getEmail()).thenReturn("dev@example.com");
        Mockito.when(existingTask.getKey()).thenReturn("PRO-13");
        Mockito.when(existingTask.getProjectKey()).thenReturn("PRO");
        Mockito.when(existingTask.isDeleted()).thenReturn(false);
        Mockito.when(testProject.isActive()).thenReturn(true);
        Mockito.when(testProject.getKey()).thenReturn("PRO");
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.ADMIN));
        final var getProjectResponse = new GetProjectUseCase.GetProjectResponse(testProject, true);
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(getProjectResponse);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(existingTask));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
    }

    @Test
    void editNotActive() {
        Mockito.when(testProject.isActive()).thenReturn(false);
        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);

        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void editReadAccessOnly() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);

        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.edit(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void editDeleted() {
        Mockito.when(existingTask.isDeleted()).thenReturn(true);
        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);

        assertThatExceptionOfType(TaskDeletedException.class).isThrownBy(() -> target.edit(request));

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
        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, "assigneeEmail", 1);

        target.edit(request);

        Mockito.verify(existingTask).setTitle("new title");
        Mockito.verify(existingTask).setDescription("new description");
        Mockito.verify(existingTask).setStatus(TaskStatus.TODO);
        Mockito.verify(existingTask).setAssigneeEmail("assigneeEmail");
        Mockito.verify(existingTask).setEstimation(1);
        Mockito.verify(taskRepository).save(existingTask);
    }

    @Test
    void editUnset() {
        final var request = new EditTaskRequest("PRO-13", "new title", "new description",
                TaskStatus.TODO, null, null);

        target.edit(request);

        Mockito.verify(existingTask).setTitle("new title");
        Mockito.verify(existingTask).setDescription("new description");
        Mockito.verify(existingTask).setStatus(TaskStatus.TODO);
        Mockito.verify(existingTask).setAssigneeEmail(null);
        Mockito.verify(existingTask).setEstimation(null);
        Mockito.verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteNotActive() {
        Mockito.when(testProject.isActive()).thenReturn(false);
        final var request = new DeleteTaskRequest("PRO-13");

        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.delete(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteReadAccessOnly() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        final var request = new DeleteTaskRequest("PRO-13");

        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.delete(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteDeleted() {
        Mockito.when(existingTask.isDeleted()).thenReturn(true);
        final var request = new DeleteTaskRequest("PRO-13");

        assertThatExceptionOfType(TaskDeletedException.class).isThrownBy(() -> target.delete(request));

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
        final var request = new DeleteTaskRequest("PRO-13");

        target.delete(request);

        Mockito.verify(existingTask).setDeleted();
        Mockito.verify(taskRepository).save(existingTask);
    }

    @Test
    void commentNotActive() {
        Mockito.when(testProject.isActive()).thenReturn(false);
        final var request = new CommentTaskRequest("PRO-13", "new comment");

        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void commentDeleted() {
        Mockito.when(existingTask.isDeleted()).thenReturn(true);
        final var request = new CommentTaskRequest("PRO-13", "new comment");

        assertThatExceptionOfType(TaskDeletedException.class).isThrownBy(() -> target.comment(request));

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
        final var request = new CommentTaskRequest("PRO-13", "new comment");

        target.comment(request);

        Mockito.verify(existingTask).addComment("new comment");
        Mockito.verify(taskRepository).save(existingTask);
    }

    @Test
    void commentReadAccessOnly() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        final var request = new CommentTaskRequest("PRO-13", "new comment");

        target.comment(request);

        Mockito.verify(existingTask).addComment("new comment");
        Mockito.verify(taskRepository).save(existingTask);
    }
}