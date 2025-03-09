package site.gutschi.humble.spring.users.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class UpdateUserUseCaseTest {
    @Autowired
    private UpdateUserUseCase target;

    @MockitoBean
    private CurrentUserInformation currentUserInformation;

    @MockitoBean
    private UserRepository userRepository;

    private User currentUser;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(currentUserInformation.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserInformation.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void firstLogin() {
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.empty());
        final var request = new UpdateUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

        final var result = target.updateUserAfterLogin(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(request.email());
        assertThat(result.getName()).isEqualTo(request.name());
        Mockito.verify(userRepository, Mockito.times(1)).save(result);
    }

    @Test
    void existingUser() {
        final var request = new UpdateUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

        final var result = target.updateUserAfterLogin(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(request.email());
        assertThat(result.getName()).isEqualTo(request.name());
        Mockito.verify(userRepository, Mockito.times(1)).save(result);
    }

    @Test
    void wrongUser() {
        final var otherUser = User.builder().email("new@example.com").name("Hans2").build();
        Mockito.when(currentUserInformation.getCurrentUser()).thenReturn(otherUser);
        final var request = new UpdateUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.updateUserAfterLogin(request));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }
}