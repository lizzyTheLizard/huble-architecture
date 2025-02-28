package site.gutschi.humble.spring.integration.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:keycloak.properties")
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http.oauth2Login(Customizer.withDefaults());
        http.csrf(Customizer.withDefaults());
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            final var idToken = authorities.stream()
                    .filter(a -> a instanceof OidcUserAuthority)
                    .map(a -> (OidcUserAuthority) a)
                    .findFirst().orElseThrow().getIdToken();
            final var result = new LinkedList<GrantedAuthority>(authorities);
            final var roles = getRealmRolesFromToken(idToken);
            log.debug("Roles from token: {}", roles);
            result.addAll(roles);
            return result;
        };
    }

    private Collection<? extends GrantedAuthority> getRealmRolesFromToken(OidcIdToken idToken) {
        try {
            final var realmAccess = idToken.getClaim("realm_access");
            if (realmAccess == null) {
                return List.of();
            }
            final var realmAccessMap = (Map<?, ?>) realmAccess;
            final var realmRoles = realmAccessMap.get("roles");
            if (realmRoles == null) {
                return List.of();
            }
            final var realmRolesList = (Collection<?>) realmRoles;
            return realmRolesList.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()))
                    .toList();
        } catch (Exception e) {
            log.warn("Could not extract realm roles from token", e);
            return List.of();
        }
    }
}
