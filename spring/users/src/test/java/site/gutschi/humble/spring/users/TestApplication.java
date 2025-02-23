package site.gutschi.humble.spring.users;


import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    @Bean
    ProjectRepository projectRepository() {
        return Mockito.mock(ProjectRepository.class);
    }
}
