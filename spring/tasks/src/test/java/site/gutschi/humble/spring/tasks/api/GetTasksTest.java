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
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.ports.SearchCallerResponse;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class GetTasksTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private GetTasksUseCase target;

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
    private SearchCaller searchCaller;

    @BeforeEach
    void setup() {
        Mockito.when(existingTask.getProjectKey()).thenReturn("PRO");
        Mockito.when(testProject.getKey()).thenReturn("PRO");
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(testProject.getProjectRoles()).thenReturn(Set.of(new ProjectRole(currentUser, ProjectRoleType.DEVELOPER)));
        final var getProjectResponse = new GetProjectUseCase.GetProjectResponse(testProject, true);
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(getProjectResponse);
        Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(Set.of(testProject));
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.of(existingTask));
        Mockito.when(currentUser.getEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    void getNonExistingTask() {
        Mockito.when(taskRepository.findByKey("PRO-13")).thenReturn(Optional.empty());

        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> target.getTaskByKey("PRO-12"));
    }

    @Test
    void getTask() {
        final var result = target.getTaskByKey("PRO-13");

        assertThat(result.task()).isEqualTo(existingTask);
        assertThat(result.deletable()).isEqualTo(false);
        assertThat(result.editable()).isEqualTo(true);
        assertThat(result.project()).isEqualTo(testProject);
    }

    @Test
    void findTasks() {
        final var searchRequest = new SearchCallerRequest("test", 1, 10, Set.of(testProject));
        final var taskView = Mockito.mock(FindTasksResponse.TaskFindView.class);
        final var searchResponse = new SearchCallerResponse(List.of(taskView), 3);
        Mockito.when(searchCaller.findTasks(Mockito.eq(searchRequest))).thenReturn(searchResponse);
        final var request = new FindTasksRequest("test", 1, 10);

        final var result = target.findTasks(request);

        assertThat(result.tasks()).containsExactly(taskView);
        assertThat(result.projects()).containsExactly(testProject);
        assertThat(result.total()).isEqualTo(3);
    }
}
