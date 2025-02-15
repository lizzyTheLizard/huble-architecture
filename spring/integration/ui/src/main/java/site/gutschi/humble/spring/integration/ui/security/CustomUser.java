package site.gutschi.humble.spring.integration.ui.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUser implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUser(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = user.isSystemAdmin()
                ? List.of(new SimpleGrantedAuthority("ROLE_SYSTEM_ADMIN"))
                : List.of();
    }
}
