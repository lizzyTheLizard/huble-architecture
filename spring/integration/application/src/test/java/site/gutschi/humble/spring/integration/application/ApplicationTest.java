package site.gutschi.humble.spring.integration.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.test.KeycloakContainer;
import site.gutschi.humble.spring.common.test.PostgresContainer;
import site.gutschi.humble.spring.common.test.SolrContainer;

@SpringBootTest()
@Testcontainers
class ApplicationTest {
    @Container
    static final SolrContainer solrContainer = new SolrContainer().withConfigDir("solr");

    @Container
    static final KeycloakContainer keycloakContainer = new KeycloakContainer().withConfigFile("keycloak-config.json");

    @Container
    static final PostgresContainer postgresContainer = new PostgresContainer();

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        postgresContainer.registerProperties(registry);
        keycloakContainer.registerProperties(registry);
        solrContainer.registerProperties(registry);
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(postgresContainer).isNotNull();
    }

}