package site.gutschi.humble.spring.users.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class ShowProjectsUseCaseTest {
    @Autowired
    private ShowProjectsUseCase target;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProjectRepository projectRepository;

    private User currentUser;
    private Project testProject;

    @BeforeEach
    void setup() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        testProject = Project.createNew("PRO", "Test Project", currentUser, currentUserApi);
        testProject.setUserRole(currentUser, ProjectRoleType.STAKEHOLDER);
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Nested
    class GetProject {
        @BeforeEach
        void setup() {
            Mockito.when(userRepository.findByMail(Mockito.anyString())).thenReturn(Optional.empty());
            Mockito.when(userRepository.findByMail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
            Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.of(testProject));
        }

        @Test
        void notAllowed() {
            testProject.removeUserRole(currentUser);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getProject(testProject.getKey()));
        }

        @Test
        void notExisting() {
            Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.getProject(testProject.getKey()));
        }

        @Test
        void asSystemUser() {
            testProject.removeUserRole(currentUser);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);

            final var result = target.getProject(testProject.getKey());

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(new ShowProjectsUseCase.GetProjectResponse(testProject, true));
        }

        @Test
        void asUser() {
            final var result = target.getProject(testProject.getKey());

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(new ShowProjectsUseCase.GetProjectResponse(testProject, false));
        }
    }

    @Nested
    class GetAllProjects {
        private Project testProject2;

        @BeforeEach
        void setup() {
            final var testUser = User.builder().email("dev2@example.com").name("Hans").build();
            testProject2 = Project.createNew("PRO", "Test Project", testUser, currentUserApi);
            Mockito.when(projectRepository.findAll()).thenReturn(Set.of(testProject, testProject2));
        }

        @Test
        void notAllowed() {
            testProject.removeUserRole(currentUser);

            assertThat(target.getAllProjects()).isEmpty();
        }

        @Test
        void asSystemUser() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);

            assertThat(target.getAllProjects()).contains(testProject, testProject2);
        }

        @Test
        void asUser() {
            assertThat(target.getAllProjects()).contains(testProject);
        }
    }
}