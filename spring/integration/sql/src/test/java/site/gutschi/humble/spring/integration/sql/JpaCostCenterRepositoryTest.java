package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaCostCenterRepositoryTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("resource") // Closed by Spring
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private CostCenterRepository costCenterRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    private Project project;

    @BeforeEach
    void setup() {
        final var user = new User("dev@example.com", "Hans");
        userRepository.save(user);
        project = Project.createNew("PRO", "Test", user, currentUserApi);
        projectRepository.save(project);
    }


    @Test
    void saveAndReload() {
        final var costCenter = CostCenter.create("Cost Center", List.of("A11", "A2"), "cc@example.com");

        final var result = costCenterRepository.save(costCenter);
        assertThat(result.getId()).isNotNull();
        assertThat(result.isDeleted()).isEqualTo(costCenter.isDeleted());
        assertThat(result.getName()).isEqualTo(costCenter.getName());
        assertThat(result.getEmail()).isEqualTo(costCenter.getEmail());
        assertThat(result.getAddress()).isEqualTo(costCenter.getAddress());

        final var result2 = costCenterRepository.findById(result.getId());
        assertThat(result2).isNotEmpty();
        assertThat(result2.get().isDeleted()).isEqualTo(costCenter.isDeleted());
        assertThat(result2.get().getName()).isEqualTo(costCenter.getName());
        assertThat(result2.get().getEmail()).isEqualTo(costCenter.getEmail());
        assertThat(result2.get().getAddress()).isEqualTo(costCenter.getAddress());
    }

    @Test
    void addProject() {
        final var costCenter = costCenterRepository.save(CostCenter.create("Cost Center", List.of("A11", "A2"), "cc@example.com"));

        costCenter.addProject(project);
        final var result = costCenterRepository.save(costCenter);

        assertThat(result.getProjects().stream().map(Project::getKey)).containsExactly(project.getKey());
    }

    @Test
    void removeProject() {
        final var costCenter = costCenterRepository.save(CostCenter.create("Cost Center", List.of("A11", "A2"), "cc@example.com"));
        costCenter.addProject(project);
        costCenterRepository.save(costCenter);

        costCenter.removeProject(project);
        final var result = costCenterRepository.save(costCenter);

        assertThat(result.getProjects().stream().map(Project::getKey)).isEmpty();
    }

    @Test
    void findAll() {
        final var costCenter = costCenterRepository.save(CostCenter.create("Cost Center", List.of("A11", "A2"), "cc@example.com"));

        assertThat(costCenterRepository.findAll().stream().map(CostCenter::getId)).contains(costCenter.getId());
    }
}