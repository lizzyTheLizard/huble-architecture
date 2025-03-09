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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import site.gutschi.humble.spring.users.api.UpdateUserUseCase;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:keycloak.properties")
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {
    private final UpdateUserUseCase updateUserUseCase;

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
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final var result = new OidcUserService();
        result.setOidcUserMapper(this::mapUser);
        return result;
    }

    private OidcUser mapUser(OidcUserRequest oidcUserRequest, OidcUserInfo oidcUserInfo) {
        final var email = oidcUserInfo.getEmail();
        final var name = oidcUserInfo.getFullName();
        final var request = new UpdateUserUseCase.UpdateUserRequest(email, name);
        final var user = updateUserUseCase.updateUserAfterLogin(request);
        final var idToken = oidcUserRequest.getIdToken();
        final var authorities = getAuthorities(idToken);
        final var claims = idToken.getClaims();
        return new CustomOidcUser(user, authorities, idToken, oidcUserInfo, claims, email);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(OidcIdToken idToken) {
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
    }
}
