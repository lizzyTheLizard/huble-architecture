package site.gutschi.humble.spring.tasks.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.*;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class CommentTaskTest {
    @Autowired
    private CommentTaskUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    @Test
    void commentNotActive() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(false);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void commentWithoutAccess() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(task.isDeleted()).thenReturn(false);
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.isActive()).thenReturn(true);
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.empty());
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.comment(request));

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
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.comment(request));

        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void commentNotFound() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.comment(request));

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
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

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
        Mockito.when(getProjectApi.getProject("PRO")).thenReturn(Optional.of(project));

        final var request = new CommentTaskRequest("PRO-13", "new comment");
        target.comment(request);

        Mockito.verify(task).addComment("new comment");
        Mockito.verify(taskRepository).save(task);
    }
}