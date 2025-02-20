package site.gutschi.humble.spring.users.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditUserUseCaseTest {
    @Autowired
    private EditUserUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProjectRepository projectRepository;

    private User currentUser;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
    }

    @Nested
    class UpdateUser {
        @Test
        void firstLogin() {
            Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.empty());
            final var request = new EditUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

            final var result = target.updateUserAfterLogin(request);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(request.email());
            assertThat(result.getName()).isEqualTo(request.name());
            Mockito.verify(userRepository, Mockito.times(1)).save(result);
        }

        @Test
        void existingUser() {
            final var request = new EditUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

            final var result = target.updateUserAfterLogin(request);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(request.email());
            assertThat(result.getName()).isEqualTo(request.name());
            Mockito.verify(userRepository, Mockito.times(1)).save(result);
        }

        @Test
        void wrongUser() {
            Mockito.when(currentUserApi.currentEmail()).thenReturn("new@example.com");
            final var request = new EditUserUseCase.UpdateUserRequest(currentUser.getEmail(), "New Name");

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.updateUserAfterLogin(request));
            Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
        }
    }

    @Nested
    class EditUsers {
        private User testUser;
        private Project testProject;

        @BeforeEach
        void setup() {
            testUser = User.builder().email("test@example.com").name("Dev").build();
            testProject = Project.createNew("PRO", "Test Project", currentUser, currentUserApi);
            testProject.setUserRole(testUser, ProjectRoleType.ADMIN);
            Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
            Mockito.when(projectRepository.findAllForUser(testUser)).thenReturn(Set.of(testProject));
        }

        @Test
        void notAllowed() {
            testProject.setUserRole(testUser, ProjectRoleType.DEVELOPER);
            final var request = new EditUserUseCase.EditUserRequest("New Name", testUser.getEmail());

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.editUser(request));
            Mockito.verify(userRepository, Mockito.never()).save(testUser);
        }

        @Test
        void notVisible() {
            Mockito.when(projectRepository.findAllForUser(testUser)).thenReturn(Set.of());
            testProject.removeUserRole(testUser);
            final var request = new EditUserUseCase.EditUserRequest("New Name", testUser.getEmail());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.editUser(request));
            Mockito.verify(userRepository, Mockito.never()).save(testUser);
        }

        @Test
        void notFound() {
            Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
            final var request = new EditUserUseCase.EditUserRequest("New Name", testUser.getEmail());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.editUser(request));
            Mockito.verify(userRepository, Mockito.never()).save(testUser);
        }

        @Test
        void myself() {
            final var request = new EditUserUseCase.EditUserRequest("New Name", currentUser.getEmail());

            target.editUser(request);

            assertThat(currentUser.getName()).isEqualTo("New Name");
            Mockito.verify(userRepository, Mockito.atLeastOnce()).save(currentUser);
        }

        @Test
        void asSystemUser() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            final var request = new EditUserUseCase.EditUserRequest("New Name", testUser.getEmail());

            target.editUser(request);

            assertThat(testUser.getName()).isEqualTo("New Name");
            Mockito.verify(userRepository, Mockito.atLeastOnce()).save(testUser);
        }
    }
}