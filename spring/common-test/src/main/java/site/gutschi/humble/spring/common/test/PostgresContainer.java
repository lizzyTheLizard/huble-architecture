package site.gutschi.humble.spring.common.test;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

public class PostgresContainer extends GenericContainer<PostgresContainer> {
    private static final String IMAGE_NAME = "postgres";
    private static final String IMAGE_VERSION = "16-alpine";
    private static final int POSTGRESQL_PORT = 5432;
    private static final String POSTGRES_DB = "test";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "password";

    public PostgresContainer() {
        super(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_VERSION));
    }

    protected void configure() {
        addExposedPort(POSTGRESQL_PORT);
        addEnv("POSTGRES_DB", POSTGRES_DB);
        addEnv("POSTGRES_USER", POSTGRES_USER);
        addEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD);
        setCommand("postgres", "-c", "fsync=off");
        setWaitStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2));
        setLogConsumers(List.of(new Slf4jLogConsumer(logger())));
    }

    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("site.gutschi.humble.spring.integration.db.url", this::getUrl);
        registry.add("site.gutschi.humble.spring.integration.db.username", () -> POSTGRES_USER);
        registry.add("site.gutschi.humble.spring.integration.db.password", () -> POSTGRES_PASSWORD);
        registry.add("site.gutschi.humble.spring.integration.db.show-sql", () -> true);
    }

    public String getUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s",
                getHost(),
                getMappedPort(POSTGRESQL_PORT),
                POSTGRES_DB);
    }
}
