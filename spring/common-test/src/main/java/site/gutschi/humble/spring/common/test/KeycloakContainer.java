package site.gutschi.humble.spring.common.test;

import org.slf4j.Logger;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.DockerLoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class KeycloakContainer extends GenericContainer<KeycloakContainer> {
    private static final String IMAGE_VERSION = "26.1";
    private static final String IMAGE_NAME = "quay.io/keycloak/keycloak";
    private static final int HTTP_PORT = 8080;
    private static final String CONFIG_FILE = "/opt/keycloak/data/import/keycloak-config.json";
    private static final String KC_BOOTSTRAP_ADMIN_USERNAME = "admin";
    private static final String KC_BOOTSTRAP_ADMIN_PASSWORD = "admin";
    private String configFile = null;

    public KeycloakContainer() {
        super(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_VERSION));
    }

    public KeycloakContainer withConfigFile(String configFile) {
        this.configFile = configFile;
        return this;
    }

    @Override
    protected void configure() {
        super.configure();
        addExposedPort(HTTP_PORT);
        addEnv("KC_BOOTSTRAP_ADMIN_USERNAME", KC_BOOTSTRAP_ADMIN_USERNAME);
        addEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", KC_BOOTSTRAP_ADMIN_PASSWORD);
        addEnv("DB_VENDOR", "h2");
        setWaitStrategy(new LogMessageWaitStrategy()
                .withRegEx(".*Keycloak [\\d\\.]* on JVM \\(powered by Quarkus [\\d\\.]*\\) started in [\\d\\.]*s. Listening on: http.*")
                .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
        setLogConsumers(List.of(new Slf4jLogConsumer(logger())));

        if (configFile != null) {
            withClasspathResourceMapping(configFile, CONFIG_FILE, BindMode.READ_ONLY);
            setCommand("start-dev --import-realm");
        }
    }


    protected Logger logger() {
        return DockerLoggerFactory.getLogger(this.getDockerImageName());
    }

    public String getKeycloakBaseUrl() {
        return "http://localhost:" + getMappedPort(HTTP_PORT);
    }

    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("site.gutschi.humble.spring.keycloak.url", () -> getKeycloakBaseUrl() + "/realms/TestExample");
    }
}
