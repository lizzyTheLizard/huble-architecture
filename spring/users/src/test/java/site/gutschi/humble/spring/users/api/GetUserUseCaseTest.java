package site.gutschi.humble.spring.users.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
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
class GetUserUseCaseTest {
    @Autowired
    private GetUserUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProjectRepository projectRepository;

    private User currentUser;
    private User testUser;
    private Project testProject;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testUser = User.builder().email("test@example.com").name("Dev").build();
        testProject = Project.createNew("PRO", "Test Project", currentUser, currentUserApi);
        testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);
        testProject.setUserRole(testUser, ProjectRoleType.STAKEHOLDER);
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(projectRepository.findAllForUser(testUser)).thenReturn(Set.of(testProject));
        Mockito.when(projectRepository.findAllForUser(currentUser)).thenReturn(Set.of(testProject));
    }

    @Test
    void nonExisting() {
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getUser(testUser.getEmail()));
    }

    @Test
    void notAllowed() {
        testProject.removeUserRole(currentUser);

        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getUser(testUser.getEmail()));
    }

    @Test
    void asSystemUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);

        final var result = target.getUser(testUser.getEmail());

        assertThat(result).isEqualTo(testUser);
    }

    @Test
    void sameProject() {
        final var result = target.getUser(testUser.getEmail());

        assertThat(result).isEqualTo(testUser);
    }
}