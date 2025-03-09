package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.common.test.PostgresContainer;
import site.gutschi.humble.spring.users.model.*;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ProjectRepositoryTests {
    final static User u1 = User.builder().email("u1@example.com").name("U1").build();
    final static User u2 = User.builder().email("u2@example.com").name("U2").build();
    final static User u3 = User.builder().email("u3@example.com").name("U3").build();

    @Container
    static final PostgresContainer container = new PostgresContainer();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    private static Stream<Project> provideProject() {
        final var role1 = new ProjectRole(u1, ProjectRoleType.STAKEHOLDER);
        final var role2 = new ProjectRole(u2, ProjectRoleType.DEVELOPER);
        TimeHelper.setNow(Instant.ofEpochMilli(10000));
        final var hEntry1 = new ProjectHistoryEntry(u1, TimeHelper.now(), ProjectHistoryType.CREATED, null, null, null);
        TimeHelper.setNow(Instant.ofEpochMilli(10100));
        final var hEntry2 = new ProjectHistoryEntry(u2, TimeHelper.now(), ProjectHistoryType.USER_ROLE_CHANGED, u1, "STAKEHOLDER", "ADMIN");
        TimeHelper.setNow(null);

        final var projectBuilder = Project.builder()
                .key("TXT")
                .name("Test Project")
                .active(true)
                .estimation(1)
                .estimation(3);
        TimeHelper.setNow(Instant.ofEpochMilli(10000));
        return Stream.of(
                projectBuilder.build(),
                projectBuilder.active(false).build(),
                projectBuilder.estimation(5).build(),
                projectBuilder.projectRole(role1).build(),
                projectBuilder.projectRole(role1).projectRole(role2).build(),
                projectBuilder.historyEntry(hEntry1).build(),
                projectBuilder.historyEntry(hEntry1).historyEntry(hEntry2).build()
        );
    }

    @BeforeEach
    void setup() {
        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
    }

    @ParameterizedTest
    @MethodSource("provideProject")
    void saveAndReload(Project project) {
        projectRepository.save(project);

        final var result = projectRepository.findByKey(project.getKey());
        assertThat(result).isPresent();
        assertThat(result.get().getKey()).isEqualTo(project.getKey());
        assertThat(result.get().getName()).isEqualTo(project.getName());
        assertThat(result.get().getEstimations()).containsExactlyInAnyOrder(project.getEstimations().toArray(Integer[]::new));

        assertThat(result.get().getProjectRoles()).hasSize(project.getProjectRoles().size());
        for (ProjectRole role : project.getProjectRoles()) {
            assertThat(result.get().getRole(role.user())).contains(role.type());
        }
        assertThat(result.get().getHistoryEntries()).hasSize(project.getHistoryEntries().size());
        result.get().getHistoryEntries().forEach(resultEntry -> {
            final var taskEntry = project.getHistoryEntries().stream().filter(e -> e.description().equals(resultEntry.description())).findFirst();
            assertThat(taskEntry).isPresent();
            assertThat(resultEntry.user()).isEqualTo(taskEntry.get().user());
            assertThat(resultEntry.timestamp()).isEqualTo(taskEntry.get().timestamp());
            assertThat(resultEntry.type()).isEqualTo(taskEntry.get().type());
            assertThat(resultEntry.oldValue()).isEqualTo(taskEntry.get().oldValue());
            assertThat(resultEntry.newValue()).isEqualTo(taskEntry.get().newValue());
        });
        assertThat(result.get().isActive()).isEqualTo(project.isActive());
    }

    @Test
    void notFound() {
        final var result = projectRepository.findByKey("WRONG");
        assertThat(result).isEmpty();
    }

    @Test
    void findAll() {
        final var project = Project.builder()
                .key("FA")
                .name("Find All")
                .build();
        projectRepository.save(project);
        final var project2 = Project.builder()
                .key("FA 2")
                .name("Find All 2")
                .build();
        projectRepository.save(project2);

        final var result = projectRepository.findAll().stream().map(Project::getKey);
        assertThat(result).contains(project.getKey());
    }

    @Test
    void findForUser() {
        final var project1 = Project.builder()
                .key("FF-U1")
                .name("Find All 1")
                .projectRole(new ProjectRole(u1, ProjectRoleType.STAKEHOLDER))
                .build();
        projectRepository.save(project1);
        final var project2 = Project.builder()
                .key("FF-U2")
                .name("Find All 2")
                .projectRole(new ProjectRole(u3, ProjectRoleType.STAKEHOLDER))
                .build();
        projectRepository.save(project2);

        final var result = projectRepository.findAllForUser(u3).stream().map(Project::getKey).toList();
        assertThat(result).contains(project2.getKey());
        assertThat(result).doesNotContain(project1.getKey());
    }
}
