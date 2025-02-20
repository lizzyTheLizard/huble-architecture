package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.EditUserUseCase;
import site.gutschi.humble.spring.users.api.GetUserUseCase;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements EditUserUseCase, GetUserUseCase {
    private final UserRepository userRepository;
    private final CanAccessUserPolicy canAccessUserPolicy;
    private final KeyUniquePolicy keyUniquePolicy;
    private final UserValidPolicy userValidPolicy;

    @Override
    public User getUser(String userEmail) {
        final var user = userRepository.findByMail(userEmail)
                .orElseThrow(() -> canAccessUserPolicy.userNotFound(userEmail));
        canAccessUserPolicy.ensureCanRead(user);
        return user;
    }

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
    public void editUser(EditUserRequest request) {
        final var user = getUser(request.email());
        canAccessUserPolicy.ensureCanEdit(user);
        user.setName(request.name());
        userValidPolicy.ensureUserValid(user);
        userRepository.save(user);
        log.info("User {} edited", user.getEmail());
    }

}
