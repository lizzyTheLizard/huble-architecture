package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CanAccessUserPolicy {
    private final CurrentUserApi currentUserApi;

    public NotFoundException userNotFound(String email) {
        final var currentUser = currentUserApi.currentEmail();
        throw NotFoundException.notFound("User", email, currentUser);
    }

    public void canUpdateAfterLogin(String email) {
        if (currentUserApi.currentEmail().equals(email)) return;
        if (currentUserApi.isSystemAdmin()) return;
        throw NotAllowedException.notAllowed("User", email, "update", currentUserApi.currentEmail());
    }
}
