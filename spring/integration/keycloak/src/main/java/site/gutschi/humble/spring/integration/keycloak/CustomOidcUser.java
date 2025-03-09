package site.gutschi.humble.spring.integration.keycloak;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.Map;

@Data
public class CustomOidcUser implements OidcUser {
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> claims;
    private final String name;
    private Map<String, Object> attributes;
}
