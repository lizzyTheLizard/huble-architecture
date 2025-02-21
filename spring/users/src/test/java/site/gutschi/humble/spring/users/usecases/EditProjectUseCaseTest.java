package site.gutschi.humble.spring.users.usecases;

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
class EditProjectUseCaseTest {
    @Autowired
    private EditProjectUseCase target;

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
        testProject.setUserRole(currentUser, ProjectRoleType.ADMIN);
        Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.of(testProject));
        //Mockito.when(projectRepository.findAll()).thenReturn(Set.of(testProject));
        Mockito.when(currentUserApi.currentEmail()).thenReturn(currentUser.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Nested
    class EditProject {
        @Test
        void projectNotAllowed() {
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            final var request = new EditProjectUseCase.EditProjectRequest(testProject.getKey(), "New Name", Set.of(3, 5, 7), true);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.editProject(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotVisible() {
            testProject.removeUserRole(currentUser);
            final var request = new EditProjectUseCase.EditProjectRequest(testProject.getKey(), "New Name", Set.of(3, 5, 7), true);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.editProject(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotFound() {
            Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());
            final var request = new EditProjectUseCase.EditProjectRequest(testProject.getKey(), "New Name", Set.of(3, 5, 7), true);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.editProject(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void asSystemUser() {
            // needs at least one admin…
            testProject.setUserRole(testUser, ProjectRoleType.ADMIN);
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            final var request = new EditProjectUseCase.EditProjectRequest(testProject.getKey(), "New Name", Set.of(3, 5, 7), true);

            target.editProject(request);

            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
            assertThat(testProject.isActive()).isTrue();
            assertThat(testProject.getEstimations()).containsExactlyInAnyOrder(request.estimations().toArray(new Integer[0]));
            assertThat(testProject.getName()).isEqualTo(request.name());

        }

        @Test
        void ownProject() {
            final var request = new EditProjectUseCase.EditProjectRequest(testProject.getKey(), "New Name", Set.of(3, 5, 7), true);

            target.editProject(request);

            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
            assertThat(testProject.isActive()).isTrue();
            assertThat(testProject.getEstimations()).containsExactlyInAnyOrder(request.estimations().toArray(new Integer[0]));
            assertThat(testProject.getName()).isEqualTo(request.name());
        }
    }

    @Nested
    class AssignUser {
        @Test
        void projectNotAllowed() {
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.assignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotVisible() {
            testProject.removeUserRole(currentUser);
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.assignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotFound() {
            Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.assignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void userNotFound() {
            Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.assignUser(request));
        }

        @Test
        void asSystemUser() {
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            target.assignUser(request);

            assertThat(testProject.getRole(testUser.getEmail())).contains(ProjectRoleType.STAKEHOLDER);
            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
        }

        @Test
        void ownProject() {
            final var request = new EditProjectUseCase.AssignUserRequest(testUser.getEmail(), testProject.getKey(), ProjectRoleType.STAKEHOLDER);

            target.assignUser(request);

            assertThat(testProject.getRole(testUser.getEmail())).contains(ProjectRoleType.STAKEHOLDER);
            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
        }
    }

    @Nested
    class UnAssignUser {
        @BeforeEach
        void setup() {
            testProject.setUserRole(testUser, ProjectRoleType.DEVELOPER);
        }

        @Test
        void projectNotAllowed() {
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.unAssignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotVisible() {
            testProject.removeUserRole(currentUser);
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.unAssignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void projectNotFound() {
            Mockito.when(projectRepository.findByKey(testProject.getKey())).thenReturn(Optional.empty());
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.unAssignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void userNotFound() {
            Mockito.when(userRepository.findByMail(testUser.getEmail())).thenReturn(Optional.empty());
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.unAssignUser(request));

            Mockito.verify(projectRepository, Mockito.never()).save(testProject);
        }

        @Test
        void asSystemUser() {
            testProject.setUserRole(currentUser, ProjectRoleType.DEVELOPER);
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            target.unAssignUser(request);

            assertThat(testProject.getRole(testUser.getEmail())).isEmpty();
            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
        }

        @Test
        void ownProject() {
            final var request = new EditProjectUseCase.UnAssignUserRequest(testUser.getEmail(), testProject.getKey());

            target.unAssignUser(request);

            assertThat(testProject.getRole(testUser.getEmail())).isEmpty();
            Mockito.verify(projectRepository, Mockito.atLeastOnce()).save(testProject);
        }
    }
}