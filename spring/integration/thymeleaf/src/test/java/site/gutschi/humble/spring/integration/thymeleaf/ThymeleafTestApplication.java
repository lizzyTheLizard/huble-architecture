package site.gutschi.humble.spring.integration.thymeleaf;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;

@SpringBootApplication(scanBasePackages = "site.gutschi.humble.spring")
public class ThymeleafTestApplication {
    @Bean
    public SearchCaller searchCaller() {
        return Mockito.mock(SearchCaller.class);
    }

}
