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
class EditUserUseCaseTest {
    @Autowired
    private EditUserUseCase target;

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
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(projectRepository.findAllForUser(testUser)).thenReturn(List.of(testProject));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
    }

    @Test
    void editNotAllowed() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.DEVELOPER));
        final var request = new EditUserRequest("New Name", testUser.getEmail());

        assertThatExceptionOfType(ManageUserNotAllowedException.class).isThrownBy(() -> target.editUser(request));
        Mockito.verify(userRepository, Mockito.never()).save(testUser);
    }

    @Test
    void editNotVisible() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());
        final var request = new EditUserRequest("New Name", testUser.getEmail());

        assertThatExceptionOfType(UserNotVisibleException.class).isThrownBy(() -> target.editUser(request));
        Mockito.verify(userRepository, Mockito.never()).save(testUser);
    }

    @Test
    void editNotFound() {
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
        final var request = new EditUserRequest("New Name", testUser.getEmail());

        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> target.editUser(request));
        Mockito.verify(userRepository, Mockito.never()).save(testUser);
    }

    @Test
    void editMyself() {
        final var request = new EditUserRequest("New Name", currentUser.getEmail());

        target.editUser(request);

        Mockito.verify(currentUser, Mockito.atLeastOnce()).setName("New Name");
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(currentUser);
    }

    @Test
    void editAsSystemUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        final var request = new EditUserRequest("New Name", testUser.getEmail());

        target.editUser(request);

        Mockito.verify(testUser, Mockito.atLeastOnce()).setName("New Name");
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(testUser);
    }
}