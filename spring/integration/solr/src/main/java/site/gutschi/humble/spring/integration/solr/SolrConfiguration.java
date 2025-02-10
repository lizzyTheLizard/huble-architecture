package site.gutschi.humble.spring.integration.solr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "solr")
@Data
public class SolrConfiguration {
    private String url;
}
