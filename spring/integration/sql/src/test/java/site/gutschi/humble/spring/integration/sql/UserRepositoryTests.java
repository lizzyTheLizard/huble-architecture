package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class UserRepositoryTests {
    @Container
    @ServiceConnection
    @SuppressWarnings("resource") // Closed by Spring
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private UserRepository userRepository;

    private static Stream<User> provideUsers() {
        return Stream.of(
                new User("test@example.com", "password", false, "Test User"),
                new User("admin@example.com", "password", true, "Admin"),
                new User("admin@example.com", null, true, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUsers")
    void saveAndReload(User user) {
        userRepository.save(user);

        final var result = userRepository.findByMail(user.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getName()).isEqualTo(user.getName());
        assertThat(result.get().getPassword()).isEqualTo(user.getPassword());
        assertThat(result.get().isSystemAdmin()).isEqualTo(user.isSystemAdmin());
    }

    @Test
    void notFound() {
        final var result = userRepository.findByMail("wrong@example.com");
        assertThat(result).isEmpty();
    }
}
