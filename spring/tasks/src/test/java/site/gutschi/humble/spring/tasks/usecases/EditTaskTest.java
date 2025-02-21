package site.gutschi.humble.spring.tasks.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.tasks.model.*;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditTaskTest {
    @Autowired
    private EditTaskUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @MockitoBean
    @SuppressWarnings("unused") // Used indirectly
    private SearchCaller searchCaller;

    private User currentUser;
    private Project testProject;
    private Task existingTask;

    @BeforeEach
    void setup() {
        TimeHelper.setNow(Instant.ofEpochMilli(1000));
        currentUser = new User("dev@example.com", "Hans");
        testProject = Project.createNew("PRO", "Test", currentUser, currentUserApi);
        existingTask = Task.createNew(currentUserApi, testProject.getKey(), 13, "Test", "Test");
        final var getProjectResponse = new GetProjectUseCase.GetProjectResponse(testProject, true);
        Mockito.when(getProjectUseCase.getProject(testProject.getKey())).thenReturn(getProjectResponse);
        Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.of(existingTask));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
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
            testProject.setActive(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.comment(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted();

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

            assertThat(existingTask.getComments()).singleElement().isEqualTo(new Comment(currentUser.getEmail(), Instant.ofEpochMilli(1000), request.comment()));
            Mockito.verify(taskRepository).save(existingTask);
        }

        @Test
        void readAccessOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);

            target.comment(request);

            assertThat(existingTask.getComments()).singleElement().isEqualTo(new Comment(currentUser.getEmail(), Instant.ofEpochMilli(1000), request.comment()));
            Mockito.verify(taskRepository).save(existingTask);
        }
    }

    @Nested
    class Edit {
        private EditTaskUseCase.EditTaskRequest request;

        @BeforeEach
        void setup() {
            request = new EditTaskUseCase.EditTaskRequest(existingTask.getKey(), "new title", "new description",
                    TaskStatus.TODO, "assigneeEmail", 1);
        }

        @Test
        void notActive() {
            testProject.setActive(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void readOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.edit(request));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted();

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
            assertThat(existingTask.getAssigneeEmail()).contains(request.assignee());
            assertThat(existingTask.getEstimation()).contains(request.estimation());
            assertThat(existingTask.getHistoryEntries()).contains(
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Title", "Test", "new title"),
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Description", "Test", "new description"),
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Status", TaskStatus.FUNNEL.name(), TaskStatus.TODO.name()),
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Assignee", null, "assigneeEmail"),
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Estimation", null, "1")
            );
            Mockito.verify(taskRepository).save(existingTask);
        }

        @Test
        void unsetFields() {
            existingTask.setAssigneeEmail("old@example.com");
            existingTask.setEstimation(2);
            final var request = new EditTaskUseCase.EditTaskRequest(existingTask.getKey(), "new title", "new description",
                    TaskStatus.TODO, null, null);

            target.edit(request);

            assertThat(existingTask.getAssigneeEmail()).isEmpty();
            assertThat(existingTask.getEstimation()).isEmpty();
            assertThat(existingTask.getHistoryEntries()).contains(
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Assignee", "old@example.com", null),
                    new TaskHistoryEntry(currentUser.getEmail(), Instant.ofEpochMilli(1000), TaskHistoryType.EDITED, "Estimation", "2", null)
            );
            Mockito.verify(taskRepository).save(existingTask);
        }
    }

    @Nested
    class Delete {
        @Test
        void notActive() {
            testProject.setActive(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.delete(existingTask.getKey()));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void readAccessOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.delete(existingTask.getKey()));

            Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void deleted() {
            existingTask.setDeleted();

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