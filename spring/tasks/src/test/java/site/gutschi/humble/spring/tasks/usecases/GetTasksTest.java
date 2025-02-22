package site.gutschi.humble.spring.tasks.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;
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
    private GetTasksUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private GetProjectUseCase getProjectUseCase;

    @MockitoBean
    private SearchCaller searchCaller;

    private Project testProject;
    private User currentUser;

    @BeforeEach
    void setup() {
        currentUser = new User("dev@example.com", "Hans");
        testProject = Project.createNew("PRO", "Test", currentUser, currentUserApi);
    }
    
    @Nested
    class FindTask {
        private GetTasksUseCase.TaskFindView taskView;
        private GetTasksUseCase.FindTasksRequest request;

        @BeforeEach
        void setup() {
            final var searchRequest = new SearchCaller.SearchCallerRequest("test", 1, 10, Set.of(testProject));
            taskView = new GetTasksUseCase.TaskFindView(new TaskKey(testProject.getKey(), 13), "Test", null, TaskStatus.BACKLOG);
            request = new GetTasksUseCase.FindTasksRequest("test", 1, 10);
            final var searchResponse = new SearchCaller.SearchCallerResponse(List.of(taskView), 3);
            Mockito.when(searchCaller.findTasks(Mockito.eq(searchRequest))).thenReturn(searchResponse);
            Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(Set.of(testProject));
        }

        @Test
        void findTasks() {
            final var result = target.findTasks(request);

            assertThat(result.tasks()).containsExactly(taskView);
            assertThat(result.projects()).containsExactly(testProject);
            assertThat(result.total()).isEqualTo(3);
        }
    }

    @Nested
    class GetTask {
        private Task existingTask;

        @BeforeEach
        void setup() {
            existingTask = Task.createNew(currentUserApi, testProject.getKey(), 13, "Test", "Test");
            Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.of(existingTask));
            Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
            final var getProjectResponse = new GetProjectUseCase.GetProjectResponse(testProject, true);
            Mockito.when(getProjectUseCase.getProject(testProject.getKey())).thenReturn(getProjectResponse);
        }

        @Test
        void notFound() {
            Mockito.when(taskRepository.findByKey(existingTask.getKey().toString())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getTaskByKey(existingTask.getKey()));
        }

        @Test
        void deleted() {
            existingTask.setDeleted();

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getTaskByKey(existingTask.getKey()));
        }

        @Test
        void readOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);
            final var result = target.getTaskByKey(existingTask.getKey());

            assertThat(result.task()).isEqualTo(existingTask);
            assertThat(result.deletable()).isEqualTo(false);
            assertThat(result.editable()).isEqualTo(false);
            assertThat(result.project()).isEqualTo(testProject);
        }

        @Test
        void getTask() {
            final var result = target.getTaskByKey(existingTask.getKey());

            assertThat(result.task()).isEqualTo(existingTask);
            assertThat(result.deletable()).isEqualTo(true);
            assertThat(result.editable()).isEqualTo(true);
            assertThat(result.project()).isEqualTo(testProject);
        }
    }

    @Nested
    class GetTaskForProject {
        private Task existingTask;

        @BeforeEach
        void setup() {
            existingTask = Task.createNew(currentUserApi, testProject.getKey(), 13, "Test", "Test");
            Mockito.when(taskRepository.findByProject(testProject)).thenReturn(Set.of(existingTask));
            Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
            final var getProjectResponse = new GetProjectUseCase.GetProjectResponse(testProject, true);
            Mockito.when(getProjectUseCase.getProject(testProject.getKey())).thenReturn(getProjectResponse);
        }

        @Test
        void readOnly() {
            testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);
            final var result = target.getTasksForProject(testProject.getKey());

            assertThat(result.tasks()).singleElement().isEqualTo(existingTask);
            assertThat(result.deletable()).isEqualTo(false);
            assertThat(result.editable()).isEqualTo(false);
            assertThat(result.project()).isEqualTo(testProject);
        }

        @Test
        void getTasks() {
            final var result = target.getTasksForProject(testProject.getKey());

            assertThat(result.tasks()).singleElement().isEqualTo(existingTask);
            assertThat(result.deletable()).isEqualTo(true);
            assertThat(result.editable()).isEqualTo(true);
            assertThat(result.project()).isEqualTo(testProject);
        }
    }


}
