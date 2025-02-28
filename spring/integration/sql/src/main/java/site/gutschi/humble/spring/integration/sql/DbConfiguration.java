package site.gutschi.humble.spring.integration.sql;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "site.gutschi.humble.spring.integration.sql.repo")
@EntityScan(basePackages = "site.gutschi.humble.spring.integration.sql.entity")
@PropertySource("classpath:sql.properties")
public class DbConfiguration {
}
