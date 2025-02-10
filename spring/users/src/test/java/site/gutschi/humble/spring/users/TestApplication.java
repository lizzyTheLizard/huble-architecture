package site.gutschi.humble.spring.users;


import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    @Bean
    UserRepository userRepository() {
        final var user = Mockito.mock(User.class);
        Mockito.when(user.getEmail()).thenReturn("test@example.com");
        final var userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findByMail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByMail("test@example.com")).thenReturn(Optional.of(user));
        return userRepository;
    }

    @Bean
    CurrentUserApi currentUserApi() {
        final var currentUserApi = Mockito.mock(CurrentUserApi.class);
        Mockito.when(currentUserApi.currentEmail()).thenReturn("test@example.com");
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        return currentUserApi;
    }

}
