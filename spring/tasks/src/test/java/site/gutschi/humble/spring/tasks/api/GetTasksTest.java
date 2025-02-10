package site.gutschi.humble.spring.tasks.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.TestApplication;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.ports.SearchCallerResponse;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class GetTasksTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private GetTasksUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @MockitoBean
    private SearchCaller searchCaller;

    @Test
    void getNonExistingTask() {
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.empty());

        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> target.getTaskByKey("PRO-12"));
    }

    @Test
    void getTask() {
        final var task = Mockito.mock(Task.class);
        Mockito.when(task.getProjectKey()).thenReturn("PRO");
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.of(task));
        final var project = Mockito.mock(Project.class);
        Mockito.when(project.getKey()).thenReturn("PRO");
        Mockito.when(project.getRole(TestApplication.CURRENT_USER.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(project.getProjectRoles()).thenReturn(List.of(new ProjectRole(TestApplication.CURRENT_USER, ProjectRoleType.DEVELOPER)));
        Mockito.when(getProjectUseCase.getProject("PRO")).thenReturn(Optional.of(project));

        final var result = target.getTaskByKey("PRO-12");

        assertThat(result.task()).isEqualTo(task);
        assertThat(result.deletable()).isEqualTo(false);
        assertThat(result.editable()).isEqualTo(true);
        assertThat(result.project()).isEqualTo(project);
    }

    @Test
    void findTasks() {
        final var user = Mockito.mock(User.class);
        Mockito.when(user.getEmail()).thenReturn("test@example.com");
        final var project = Mockito.mock(Project.class);
        final var projectRole1 = new ProjectRole(user, ProjectRoleType.DEVELOPER);
        final var projectRole2 = new ProjectRole(user, ProjectRoleType.ADMIN);
        Mockito.when(project.getProjectRoles()).thenReturn(List.of(projectRole1, projectRole2));
        final var projects = List.of(project);
        Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(projects);
        final var searchRequest = new SearchCallerRequest("test", 1, 10, projects);
        final var taskView = Mockito.mock(FindTasksResponse.TaskFindView.class);
        final var searchResponse = new SearchCallerResponse(List.of(taskView), 3);
        Mockito.when(searchCaller.findTasks(Mockito.eq(searchRequest))).thenReturn(searchResponse);
        final var request = new FindTasksRequest("test", 1, 10);

        final var result = target.findTasks(request);

        assertThat(result.tasks()).containsExactly(taskView);
        assertThat(result.projects()).containsExactly(project);
        assertThat(result.total()).isEqualTo(3);
    }
}
