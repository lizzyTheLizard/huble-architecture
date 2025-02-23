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
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class CreateTaskTest {
    @Autowired
    private CreateTaskUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    private User currentUser;
    private Project testProject;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test Project", currentUser, currentUserApi);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Nested
    class GetProjectToCreate {
        @BeforeEach
        void setup() {
            Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(Set.of(testProject));
        }

        @Test
        void get() {
            final var response = target.getProjectsToCreate();

            assertThat(response).singleElement().isEqualTo(testProject);
        }
    }

    @Nested
    class CreateTask {
        @BeforeEach
        void setup() {
            Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(testProject);
            Mockito.when(taskRepository.nextId("PRO")).thenReturn(42);
        }

        @Test
        void projectReadOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);

            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");
            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
        }

        @Test
        void projectNotActive() {
            testProject.setActive(false);

            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");
            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
        }

        @Test
        void ownProject() {
            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");

            final var createdTask = target.create(request);

            Mockito.verify(taskRepository).save(createdTask);
            assertThat(createdTask.getCreatorEmail()).isEqualTo(currentUser.getEmail());
            assertThat(createdTask.getKey()).isEqualTo(new TaskKey("PRO", 42));
            assertThat(createdTask.getProjectKey()).isEqualTo("PRO");
            assertThat(createdTask.getTitle()).isEqualTo("title");
            assertThat(createdTask.getDescription()).isEqualTo("description");
            assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.FUNNEL);
            assertThat(createdTask.getAssigneeEmail()).isEmpty();
            assertThat(createdTask.getEstimation()).isEmpty();
            assertThat(createdTask.getComments()).isEmpty();
            assertThat(createdTask.getHistoryEntries()).hasSize(1);
        }

        @Test
        void asSystemAdmin() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");

            final var createdTask = target.create(request);

            Mockito.verify(taskRepository).save(createdTask);
            assertThat(createdTask.getCreatorEmail()).isEqualTo(currentUser.getEmail());
            assertThat(createdTask.getKey()).isEqualTo(new TaskKey("PRO", 42));
            assertThat(createdTask.getProjectKey()).isEqualTo("PRO");
            assertThat(createdTask.getTitle()).isEqualTo("title");
            assertThat(createdTask.getDescription()).isEqualTo("description");
            assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.FUNNEL);
            assertThat(createdTask.getAssigneeEmail()).isEmpty();
            assertThat(createdTask.getEstimation()).isEmpty();
            assertThat(createdTask.getComments()).isEmpty();
            assertThat(createdTask.getHistoryEntries()).hasSize(1);
        }
    }
}