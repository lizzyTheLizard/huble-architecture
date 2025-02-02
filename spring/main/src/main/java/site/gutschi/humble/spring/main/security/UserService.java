package site.gutschi.humble.spring.main.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.UserApi;

@Service
public class UserService implements UserApi {
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
