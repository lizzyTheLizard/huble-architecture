package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class CreateTaskUseCaseTest {
    @Autowired
    private CreateTaskUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectApi getProjectApi;

    private User currentUser;
    private Project testProject;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test Project", currentUser);
        Mockito.when(currentUserApi.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Nested
    class GetProjectToCreate {
        @BeforeEach
        void setup() {
            Mockito.when(getProjectApi.getAllProjects()).thenReturn(Set.of(testProject));
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
            Mockito.when(getProjectApi.getProject("PRO")).thenReturn(testProject);
            Mockito.when(taskRepository.nextId(testProject)).thenReturn(42);
        }

        @Test
        void projectReadOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER, currentUser);

            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");
            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
        }

        @Test
        void projectNotActive() {
            testProject.setActive(false, currentUser);

            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");
            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.create(request));
        }

        @Test
        void ownProject() {
            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");

            final var createdTask = target.create(request);

            Mockito.verify(taskRepository).save(createdTask);
            assertThat(createdTask.getCreator()).isEqualTo(currentUser);
            assertThat(createdTask.getKey()).isEqualTo(new TaskKey("PRO", 42));
            assertThat(createdTask.getProject()).isEqualTo(testProject);
            assertThat(createdTask.getTitle()).isEqualTo("title");
            assertThat(createdTask.getDescription()).isEqualTo("description");
            assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.FUNNEL);
            assertThat(createdTask.getAssignee()).isEmpty();
            assertThat(createdTask.getEstimation()).isEmpty();
            assertThat(createdTask.getComments()).isEmpty();
            assertThat(createdTask.getHistoryEntries()).hasSize(1);
        }

        @Test
        void asSystemAdmin() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER, currentUser);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            CreateTaskUseCase.CreateTaskRequest request = new CreateTaskUseCase.CreateTaskRequest("PRO", "title", "description");

            final var createdTask = target.create(request);

            Mockito.verify(taskRepository).save(createdTask);
            assertThat(createdTask.getCreator()).isEqualTo(currentUser);
            assertThat(createdTask.getKey()).isEqualTo(new TaskKey("PRO", 42));
            assertThat(createdTask.getProject()).isEqualTo(testProject);
            assertThat(createdTask.getTitle()).isEqualTo("title");
            assertThat(createdTask.getDescription()).isEqualTo("description");
            assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.FUNNEL);
            assertThat(createdTask.getAssignee()).isEmpty();
            assertThat(createdTask.getEstimation()).isEmpty();
            assertThat(createdTask.getComments()).isEmpty();
            assertThat(createdTask.getHistoryEntries()).hasSize(1);
        }
    }
}