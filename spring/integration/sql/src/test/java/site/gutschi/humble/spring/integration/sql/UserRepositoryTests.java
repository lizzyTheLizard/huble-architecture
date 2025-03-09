package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.test.PostgresContainer;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class UserRepositoryTests {
    @Container
    static final PostgresContainer container = new PostgresContainer();
    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    private static Stream<User> provideUsers() {
        return Stream.of(
                User.builder().email("test@example.com").name("Test User").build(),
                User.builder().email("admin@example.com").name("Admin").build(),
                User.builder().email("admin@example.com").name(null).build()
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
    }

    @Test
    void notFound() {
        final var result = userRepository.findByMail("wrong@example.com");
        assertThat(result).isEmpty();
    }
}
