package site.gutschi.humble.spring.integration.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;

@Service
@RequiredArgsConstructor
public class CurrentUserService implements CurrentUserInformation {
    @Override
    public User getCurrentUser() {
        final var customUser = (CustomOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUser.getUser();
    }

    @Override
    public boolean isSystemAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
    }

}
