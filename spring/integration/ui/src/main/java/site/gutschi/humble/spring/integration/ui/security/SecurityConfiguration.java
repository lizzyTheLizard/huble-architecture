package site.gutschi.humble.spring.integration.ui.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // TODO: Switch to OIDC login
    // TODO: After login call updateAfterLogin
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
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll()
        );
        http.httpBasic(Customizer.withDefaults());
        http.exceptionHandling(c -> c.accessDeniedPage("/accessDenied"));
        return http.build();
    }
}
