package site.gutschi.humble.spring.main.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfiguration {
    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        final var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new CustomDialect());
        return templateEngine;
    }
}

