package site.gutschi.humble.spring.users.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class EditProjectUseCaseTest {
    @Autowired
    private EditProjectUseCase target;

    @Mock
    private User currentUser;

    @Mock
    private User testUser;

    @Mock
    private Project testProject;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        Mockito.when(currentUser.getEmail()).thenReturn("dev@example.com");
        Mockito.when(testUser.getEmail()).thenReturn("test@user.com");
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.ADMIN));
        Mockito.when(testProject.getKey()).thenReturn("PRO");
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.of(testProject));
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    void editNotAllowed() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        final var request = new EditProjectRequest(testProject.getKey(), "New Name", List.of(3, 5, 7), true);

        assertThatExceptionOfType(ManageProjectNotAllowedException.class).isThrownBy(() -> target.editProject(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void editNotVisible() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());
        final var request = new EditProjectRequest(testProject.getKey(), "New Name", List.of(3, 5, 7), true);

        assertThatExceptionOfType(ProjectNotVisibleException.class).isThrownBy(() -> target.editProject(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void editNotFound() {
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());
        final var request = new EditProjectRequest(testProject.getKey(), "New Name", List.of(3, 5, 7), true);

        assertThatExceptionOfType(ProjectNotFoundException.class).isThrownBy(() -> target.editProject(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void editAsSystemUser() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        final var request = new EditProjectRequest(testProject.getKey(), "New Name", List.of(3, 5, 7), true);

        target.editProject(request);

        Mockito.verify(testProject, Mockito.atLeastOnce()).setName("New Name");
        Mockito.verify(testProject, Mockito.atLeastOnce()).setEstimations(List.of(3, 5, 7));
        Mockito.verify(testProject, Mockito.atLeastOnce()).setActive(true);
        Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);

    }

    @Test
    void editOwnProject() {
        final var request = new EditProjectRequest(testProject.getKey(), "New Name", List.of(3, 5, 7), true);

        target.editProject(request);

        Mockito.verify(testProject, Mockito.atLeastOnce()).setName("New Name");
        Mockito.verify(testProject, Mockito.atLeastOnce()).setEstimations(List.of(3, 5, 7));
        Mockito.verify(testProject, Mockito.atLeastOnce()).setActive(true);
        Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
    }

    @Test
    void assignUserNotAllowed() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        assertThatExceptionOfType(ManageProjectNotAllowedException.class).isThrownBy(() -> target.assignUser(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void assignUserNotVisible() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        assertThatExceptionOfType(ProjectNotVisibleException.class).isThrownBy(() -> target.assignUser(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void assignUserProjectNotFound() {
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        assertThatExceptionOfType(ProjectNotFoundException.class).isThrownBy(() -> target.assignUser(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void assignUserUserNotFound() {
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> target.assignUser(request));
        Mockito.verify(projectRepository, Mockito.never()).save(testProject);
    }

    @Test
    void assignUserAsSystemUser() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        target.assignUser(request);

        Mockito.verify(testProject, Mockito.atLeastOnce()).setUserRole(testUser, ProjectRoleType.STAKEHOLDER);
        Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);

    }

    @Test
    void assignUserOwnProject() {
        final var request = new AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

        target.assignUser(request);

        Mockito.verify(testProject, Mockito.atLeastOnce()).setUserRole(testUser, ProjectRoleType.STAKEHOLDER);
        Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
    }
}