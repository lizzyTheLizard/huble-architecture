package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.CreateProjectNotAllowedException;

@Service
@RequiredArgsConstructor
public class CanCreateProjectPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanCreateProject() {
        if (currentUserApi.isSystemAdmin()) return;
        throw new CreateProjectNotAllowedException();
    }
}
