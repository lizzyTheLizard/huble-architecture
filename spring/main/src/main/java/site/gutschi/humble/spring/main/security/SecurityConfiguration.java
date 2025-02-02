package site.gutschi.humble.spring.main.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import site.gutschi.humble.spring.users.domain.api.GetUserApi;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
        );
        http.formLogin(c -> c
                .loginPage("/login.html")
                .loginProcessingUrl("/login.html")
                .failureUrl("/login.html?error=true")
                .defaultSuccessUrl("/index.html")
                .permitAll()
        );
        http.httpBasic(Customizer.withDefaults());
        http.exceptionHandling(c -> c.accessDeniedPage("/accessDenied"));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(GetUserApi userApi) {
        return username -> {
            final var password = userApi.getUser(username)
                    .map(User::getPassword)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new CustomUser(username, password);
        };
    }

    @RequiredArgsConstructor
    @Getter
    public static class CustomUser implements UserDetails {
        private final String username;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities = List.of();
    }
}
