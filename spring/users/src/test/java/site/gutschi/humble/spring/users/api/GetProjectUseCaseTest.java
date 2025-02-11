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
class GetProjectUseCaseTest {
    @Autowired
    private GetProjectUseCase target;

    @Mock
    private User currentUser;

    @Mock
    private User testUser;

    @Mock
    private Project testProject;

    @Mock
    private Project testProject2;

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
        Mockito.when(testProject.getKey()).thenReturn("PRO");
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.of(ProjectRoleType.STAKEHOLDER));
        Mockito.when(testProject2.getKey()).thenReturn("PR2");
        Mockito.when(testProject2.getRole(currentUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.of(testProject));
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(testProject, testProject2));
        Mockito.when(currentUserApi.currentEmail()).thenReturn("dev@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    void getProjectNotAllowed() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ProjectNotVisibleException.class).isThrownBy(() -> target.getProject(testProject.getKey()));
    }

    @Test
    void getProjectNotExisting() {
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ProjectNotFoundException.class).isThrownBy(() -> target.getProject(testProject.getKey()));
    }

    @Test
    void getProjectAsSystemUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());

        final var result = target.getProject(testProject.getKey());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new GetProjectResponse(testProject, true));
    }

    @Test
    void getProjectAsUser() {
        final var result = target.getProject(testProject.getKey());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new GetProjectResponse(testProject, false));
    }

    @Test
    void getProjectsNotAllowed() {
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThat(target.getAllProjects()).isEmpty();
    }

    @Test
    void getProjectsAsSystemUser() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(testProject.getRole(currentUser.getEmail())).thenReturn(Optional.empty());

        assertThat(target.getAllProjects()).contains(testProject, testProject2);
    }

    @Test
    void getProjectsAsUser() {
        assertThat(target.getAllProjects()).contains(testProject);
    }
}