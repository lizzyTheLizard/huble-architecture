package site.gutschi.humble.spring.users;

import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

@SpringBootApplication
public class UsersTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsersTestApplication.class);
    }

    @Bean
    ProjectRepository projectRepository() {
        return Mockito.mock(ProjectRepository.class);
    }

    @Bean
    UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    CurrentUserInformation currentUserInformation() {
        return Mockito.mock(CurrentUserInformation.class);
    }
}
