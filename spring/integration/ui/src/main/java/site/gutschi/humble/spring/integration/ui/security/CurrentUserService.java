package site.gutschi.humble.spring.integration.ui.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;

@Service
public class CurrentUserService implements CurrentUserApi {
    @Override
    public String currentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public boolean isSystemAdmin() {
        //TODO: Implement system admin check
        return false;
    }
}
