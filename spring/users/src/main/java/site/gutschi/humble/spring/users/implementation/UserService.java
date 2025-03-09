package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.UpdateUserUseCase;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UpdateUserUseCase, CurrentUserApi {
    private final UserRepository userRepository;
    private final CanAccessUserPolicy canAccessUserPolicy;
    private final KeyUniquePolicy keyUniquePolicy;
    private final UserValidPolicy userValidPolicy;
    private final CurrentUserInformation currentUserInformation;

    @Override
    public User updateUserAfterLogin(UpdateUserRequest request) {
        canAccessUserPolicy.canUpdateAfterLogin(request.email());
        final var existingUser = userRepository.findByMail(request.email());
        existingUser.ifPresentOrElse(
                user -> user.setName(request.name()),
                () -> keyUniquePolicy.ensureUserMailUnique(request.email())
        );
        final var user = existingUser.orElseGet(() -> User.builder()
                .name(request.name())
                .email(request.email())
                .build()
        );
        userValidPolicy.ensureUserValid(user);
        userRepository.save(user);
        if (existingUser.isEmpty()) {
            log.info("User {} created", user.getEmail());
        } else {
            log.info("User {} updated", user.getEmail());
        }
        return user;
    }

    @Override
    public User getCurrentUser() {
        return currentUserInformation.getCurrentUser();
    }

    @Override
    public boolean isSystemAdmin() {
        return currentUserInformation.isSystemAdmin();
    }
}
