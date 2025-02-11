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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class GetUserUseCaseTest {
    @Autowired
    private GetUserUseCase target;

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
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(projectRepository.findAllForUser(testUser)).thenReturn(List.of(testProject));
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    void getNonExistingUser() {
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> target.getUser(testUser.getEmail()));
    }

    @Test
    void getNotAllowedUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotVisibleException.class).isThrownBy(() -> target.getUser(testUser.getEmail()));
    }

    @Test
    void getUserAsSystemUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);

        final var result = target.getUser(testUser.getEmail());

        assertThat(result).isEqualTo(testUser);
    }

    @Test
    void getUserFromProject() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.ADMIN));

        final var result = target.getUser(testUser.getEmail());

        assertThat(result).isEqualTo(testUser);
    }
}