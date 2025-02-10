package site.gutschi.humble.spring.users.api;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectHistoryType;
import site.gutschi.humble.spring.users.model.ProjectRoleType;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class CreateProjectUseCaseTest {
    @Autowired
    private CreateProjectUseCase target;

    @Autowired
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private ProjectRepository projectRepository;

    @Test
    void createExistingProject() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.of(Mockito.mock(Project.class)));
        final var request = new CreateProjectRequest("Test Project", "PRO");

        assertThatExceptionOfType(KeyNotUniqueException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProjectWithoutSystemAdmin() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
        final var request = new CreateProjectRequest("Test Project", "PRO");

        assertThatExceptionOfType(CreateProjectNotAllowedException.class).isThrownBy(() -> target.createProject(request));
    }

    @Test
    void createProject() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(projectRepository.findByKey("PRO")).thenReturn(Optional.empty());
        final var request = new CreateProjectRequest("Test Project", "PRO");

        final var response = target.createProject(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Project");
        assertThat(response.getKey()).isEqualTo("PRO");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getEstimations()).containsExactly(1, 3, 5);
        assertThat(response.getProjectRoles()).singleElement()
                .has(new Condition<>(r -> r.user().getEmail().equals("test@example.com"), "User is test@example.com"))
                .has(new Condition<>(r -> r.type().equals(ProjectRoleType.ADMIN), "User is admin"));
        assertThat(response.getHistoryEntries()).singleElement()
                .has(new Condition<>(r -> r.user().equals("test@example.com"), "User is test@example.com"))
                .has(new Condition<>(r -> r.type().equals(ProjectHistoryType.CREATED), "Project has been created"));
    }
}