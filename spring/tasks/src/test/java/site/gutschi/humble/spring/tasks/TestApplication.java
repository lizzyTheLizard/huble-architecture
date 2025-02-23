package site.gutschi.humble.spring.tasks;

import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    @Bean
    SearchCaller searchCaller() {
        return Mockito.mock(SearchCaller.class);

    }
}
