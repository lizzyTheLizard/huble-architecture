package site.gutschi.humble.spring.tasks.integration.solr;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "solr")
@Data
public class SolrConfiguration {
    private String url;
}
