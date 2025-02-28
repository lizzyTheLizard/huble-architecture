package site.gutschi.humble.spring.integration.solr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TODO Configuration: Streamline all configuration into classes using annotation processing.

/**
 * Configuration for Solr.
 */
@Configuration
@ConfigurationProperties(prefix = "site.gutschi.humble.spring.integration.solr")
@Data
public class SolrConfiguration {
    /**
     * Solr url including the schema, host, port and the core.
     * E.g., <a href="http://localhost:8983/solr/core">http://localhost:8983/solr/core</a>.
     */
    private String url;
}
