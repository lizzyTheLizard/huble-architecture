package site.gutschi.humble.spring.users.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class CreateProjectUseCaseTest {
    @Autowired
    private CreateProjectUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private ProjectRepository projectRepository;

    @MockitoBean
    private UserRepository userRepository;

    private User currentUser;
    private Project testProject;

    @Nested
    class CanCreate {

        @Test
        void systemAdmin() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            assertThat(target.canCreateProject()).isTrue();
        }

        @Test
        void nonSystemAdmin() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
            assertThat(target.canCreateProject()).isFalse();
        }
    }

    @Nested
    class CreateProject {
        @BeforeEach
        void setup() {
            currentUser = User.builder().email("dev@example.com").name("Hans").build();
            testProject = Project.createNew("PRO", "Test Project", currentUser, currentUserApi);
            Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
            Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
            Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        }

        @Test
        void projectAlreadyExists() {
            Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.of(testProject));
            final var request = new CreateProjectUseCase.CreateProjectRequest("PRO", "Test Project");

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.createProject(request));
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
            final var request = new CreateProjectUseCase.CreateProjectRequest("PRO", "Test Project");

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.createProject(request));
        }

        @Test
        void asSystemAdmin() {
            final var request = new CreateProjectUseCase.CreateProjectRequest("PRO", "Test Project");

            final var response = target.createProject(request);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Test Project");
            assertThat(response.getKey()).isEqualTo("PRO");
            assertThat(response.isActive()).isTrue();
            assertThat(response.getEstimations()).containsExactly(1, 3, 5);
            assertThat(response.getProjectRoles()).hasSize(1);
            assertThat(response.getRole(currentUser.getEmail())).contains(ProjectRoleType.ADMIN);
            assertThat(response.getHistoryEntries()).hasSize(1);
        }
    }
}