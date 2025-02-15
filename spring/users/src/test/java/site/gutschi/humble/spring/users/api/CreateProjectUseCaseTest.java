package site.gutschi.humble.spring.users.api;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.*;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class CreateProjectUseCaseTest {
    @Autowired
    private CreateProjectUseCase target;

    @Mock
    private User currentUser;

    @Mock
    private Project testProject;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private ProjectRepository projectRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        Mockito.when(currentUser.getEmail()).thenReturn("dev@example.com");
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
    }

    @Test
    void createExistingProject() {
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.of(testProject));
        final var request = new CreateProjectRequest("PRO", "Test Project");

        assertThatExceptionOfType(KeyNotUniqueException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProjectWithoutSystemAdmin() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        final var request = new CreateProjectRequest("PRO", "Test Project");

        assertThatExceptionOfType(CreateProjectNotAllowedException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProject() {
        final var request = new CreateProjectRequest("PRO", "Test Project");

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
        final var isUser = new Condition<ProjectRole>(p -> p.user().equals(currentUser), "user " + currentUser.getEmail());
        final var isAdmin = new Condition<ProjectRole>(p -> p.type().equals(ProjectRoleType.ADMIN), "type admin");
        return allOf(isUser, isAdmin);
    }

    private Condition<ProjectHistoryEntry> created() {
        final var isUser = new Condition<ProjectHistoryEntry>(p -> p.user().equals(currentUser.getEmail()), "user " + currentUser.getEmail());
        final var isCreated = new Condition<ProjectHistoryEntry>(p -> p.type().equals(ProjectHistoryType.CREATED), "type created");
        return allOf(isUser, isCreated);
    }
}