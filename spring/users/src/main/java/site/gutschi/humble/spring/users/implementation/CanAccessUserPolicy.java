package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;

@Service
@RequiredArgsConstructor
public class CanAccessUserPolicy {
    private final CurrentUserInformation currentUserInformation;

    public NotFoundException userNotFound(String email) {
        final var currentUser = currentUserInformation.getCurrentUser().getEmail();
        throw NotFoundException.notFound("User", email, currentUser);
    }

    public void canUpdateAfterLogin(String email) {
        final var currentUser = currentUserInformation.getCurrentUser().getEmail();
        if (currentUser.equals(email)) return;
        if (currentUserInformation.isSystemAdmin()) return;
        throw NotAllowedException.notAllowed("User", email, "update", currentUser);
    }
}
