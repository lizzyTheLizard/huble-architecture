package site.gutschi.humble.spring.integration.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class ApplicationTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("resource") // Closed by Spring
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("password");


    @Test
    @SuppressWarnings("EmptyMethod")
    void contextLoads() {
    }
}