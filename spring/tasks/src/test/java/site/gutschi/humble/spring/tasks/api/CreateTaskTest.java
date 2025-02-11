package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectResponse;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class CreateTaskTest {
    @Autowired
    private CreateTaskUseCase target;

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
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(testProject.isActive()).thenReturn(true);
        final var getProjectResponse = new GetProjectResponse(testProject, true);
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(getProjectResponse);
        Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(List.of(testProject));
        Mockito.when(taskRepository.nextId("PRO")).thenReturn(42);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    void createTaskReadAccessOnly() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTaskProjectNotActive() {
        Mockito.when(testProject.isActive()).thenReturn(false);

        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        assertThatExceptionOfType(EditTaskNotAllowedException.class).isThrownBy(() -> target.create(request));
    }

    @Test
    void createTask() {
        CreateTaskRequest request = new CreateTaskRequest("PRO", "title", "description");
        final var createdTask = target.create(request);

        Mockito.verify(taskRepository).save(createdTask);
        assertThat(createdTask.getCreatorEmail()).isEqualTo(currentUser.getEmail());
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
        final var response = target.getProjectsToCreate();

        assertThat(response).singleElement().isEqualTo(testProject);
    }

}