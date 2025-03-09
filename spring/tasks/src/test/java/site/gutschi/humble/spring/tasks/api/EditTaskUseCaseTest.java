package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.tasks.model.*;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditTaskUseCaseTest {
    @Autowired
    private EditTaskUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    private User currentUser;
    private Project testProject;
    private Task existingTask;

    @BeforeEach
    void setup() {
        TimeHelper.setNow(Instant.ofEpochMilli(1000));
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test", currentUser);
        existingTask = Task.createNew(testProject, 13, "Test", "Test", currentUser);
        Mockito.when(getProjectApi.getProject(testProject.getKey())).thenReturn(testProject);
        Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.of(existingTask));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserApi.getCurrentUser()).thenReturn(currentUser);
    }

    @Nested
    class AddComment {
        private EditTaskUseCase.CommentTaskRequest request;

        @BeforeEach
        void setup() {
            request = new EditTaskUseCase.CommentTaskRequest(existingTask.getKey(), "new comment");
        }

        @Test
        void notActive() {
            testProject.setActive(false, currentUser);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.comment(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted(currentUser);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.comment(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void notFound() {
            Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.comment(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void comment() {
            target.comment(request);

            assertThat(existingTask.getComments()).singleElement().isEqualTo(new Comment(currentUser, Instant.ofEpochMilli(1000), request.comment()));
            Mockito.verify(taskRepository).save(existingTask);
        }

        @Test
        void readAccessOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER, currentUser);

            target.comment(request);

            assertThat(existingTask.getComments()).singleElement().isEqualTo(new Comment(currentUser, Instant.ofEpochMilli(1000), request.comment()));
            Mockito.verify(taskRepository).save(existingTask);
        }
    }

    @Nested
    class Edit {
        private EditTaskUseCase.EditTaskRequest request;

        @BeforeEach
        void setup() {
            final var assignee = User.builder().email("assigneeEmail").name("Assignee").build();
            request = new EditTaskUseCase.EditTaskRequest(existingTask.getKey(), "new title", "new description",
                    TaskStatus.TODO, assignee.getEmail(), 1);
            testProject.setUserRole(assignee, ProjectRoleType.DEVELOPER, currentUser);
        }

        @Test
        void notActive() {
            testProject.setActive(false, currentUser);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void readOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER, currentUser);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted(currentUser);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void notFound() {
            Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void edit() {
            target.edit(request);

            assertThat(existingTask.getTitle()).isEqualTo(request.title());
            assertThat(existingTask.getDescription()).isEqualTo(request.description());
            assertThat(existingTask.getStatus()).isEqualTo(request.status());
            assertThat(existingTask.getAssignee().map(User::getEmail)).contains(request.assignee());
            assertThat(existingTask.getEstimation()).contains(request.estimation());
            assertThat(existingTask.getHistoryEntries()).contains(
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Title", "Test", "new title"),
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Description", "Test", "new description"),
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Status", TaskStatus.FUNNEL.name(), TaskStatus.TODO.name()),
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Assignee", null, "assigneeEmail"),
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Estimation", null, "1")
            );
            Mockito.verify(taskRepository).save(existingTask);
        }

        @Test
        void unsetFields() {
            final var assignee = User.builder().email("old@example.com").name("Old").build();
            existingTask.setAssignee(assignee, currentUser);
            existingTask.setEstimation(2, currentUser);
            final var request = new EditTaskUseCase.EditTaskRequest(existingTask.getKey(), "new title", "new description",
                    TaskStatus.TODO, null, null);

            target.edit(request);

            assertThat(existingTask.getAssignee()).isEmpty();
            assertThat(existingTask.getEstimation()).isEmpty();
            assertThat(existingTask.getHistoryEntries()).contains(
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Assignee", "old@example.com", null),
                    new TaskHistoryEntry(currentUser, Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Estimation", "2", null)
            );
            Mockito.verify(taskRepository).save(existingTask);
        }
    }

    @Nested
    class Delete {
        @Test
        void notActive() {
            testProject.setActive(false, currentUser);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.delete(existingTask.getKey()));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void readAccessOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER, currentUser);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.delete(existingTask.getKey()));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted(currentUser);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.delete(existingTask.getKey()));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void notFound() {
            Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.delete(existingTask.getKey()));
        }

        @Test
        void delete() {
            target.delete(existingTask.getKey());

            assertThat(existingTask.isDeleted()).isTrue();
            Mockito.verify(taskRepository).save(existingTask);
        }
    }
}