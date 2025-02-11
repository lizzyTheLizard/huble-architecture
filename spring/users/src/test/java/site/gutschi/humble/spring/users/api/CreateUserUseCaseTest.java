package site.gutschi.humble.spring.users.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class CreateUserUseCaseTest {
    @Autowired
    private CreateUserUseCase target;

    @Mock
    private User currentUser;

    @Mock
    private User testUser;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    @SuppressWarnings("unused") // Used indirectly
    private ProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        Mockito.when(currentUser.getEmail()).thenReturn("dev@example.com");
        Mockito.when(testUser.getEmail()).thenReturn("test@user.com");
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
    }

    @Test
    void createExisting() {
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        final var request = new CreateUserRequest("New Name", testUser.getEmail());

        assertThatExceptionOfType(KeyNotUniqueException.class).isThrownBy(() -> target.createUser(request));
        Mockito.verify(userRepository, Mockito.never()).save(testUser);
    }

    @Test
    void createNotAllowed() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        final var request = new CreateUserRequest("New Name", testUser.getEmail());

        assertThatExceptionOfType(ManageUserNotAllowedException.class).isThrownBy(() -> target.createUser(request));
        Mockito.verify(userRepository, Mockito.never()).save(testUser);
    }

    @Test
    void create() {
        final var request = new CreateUserRequest("New Name", testUser.getEmail());

        final var response = target.createUser(request);

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(Mockito.any());
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
    }
}