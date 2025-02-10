package site.gutschi.humble.spring.users.api;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.TestApplication;
import site.gutschi.humble.spring.users.model.*;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class CreateProjectUseCaseTest {
    @Autowired
    private CreateProjectUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private ProjectRepository projectRepository;

    @Test
    void createExistingProject() {
        Mockito.when(currentUserApi.currentEmail()).thenReturn(TestApplication.CURRENT_USER.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.of(Mockito.mock(Project.class)));
        final var request = new CreateProjectRequest("Test Project", "PRO");

        assertThatExceptionOfType(KeyNotUniqueException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProjectWithoutSystemAdmin() {
        Mockito.when(currentUserApi.currentEmail()).thenReturn(TestApplication.CURRENT_USER.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
        final var request = new CreateProjectRequest("Test Project", "PRO");

        assertThatExceptionOfType(CreateProjectNotAllowedException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProject() {
        Mockito.when(currentUserApi.currentEmail()).thenReturn(TestApplication.CURRENT_USER.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
        final var request = new CreateProjectRequest("Test Project", "PRO");

        final var response = target.createProject(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Project");
        assertThat(response.getKey()).isEqualTo("PRO");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getEstimations()).containsExactly(1, 3, 5);
        assertThat(response.getProjectRoles()).singleElement().is(adminRole());
        assertThat(response.getHistoryEntries()).singleElement().is(created());
    }

    private Condition<ProjectRole> adminRole() {
        final var isUser = new Condition<ProjectRole>(p -> p.user().equals(TestApplication.CURRENT_USER), "user " + TestApplication.CURRENT_USER.getEmail());
        final var isAdmin = new Condition<ProjectRole>(p -> p.type().equals(ProjectRoleType.ADMIN), "type admin");
        return allOf(isUser, isAdmin);
    }

    private Condition<ProjectHistoryEntry> created() {
        final var isUser = new Condition<ProjectHistoryEntry>(p -> p.user().equals(TestApplication.CURRENT_USER.getEmail()), "user " + TestApplication.CURRENT_USER.getEmail());
        final var isCreated = new Condition<ProjectHistoryEntry>(p -> p.type().equals(ProjectHistoryType.CREATED), "type created");
        return allOf(isUser, isCreated);
    }
}