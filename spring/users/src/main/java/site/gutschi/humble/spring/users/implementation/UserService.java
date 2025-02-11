package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.*;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements CreateUserUseCase, EditUserUseCase, GetUserUseCase {
    private final UserRepository userRepository;
    private final CanAccessUserPolicy canAccessUserPolicy;
    private final KeyUniquePolicy keyUniquePolicy;

    @Override
    public User createUser(CreateUserRequest request) {
        keyUniquePolicy.ensureUserMailUnique(request.email());
        final var user = User.builder()
                .name(request.name())
                .email(request.email())
                .build();
        canAccessUserPolicy.ensureCanCreate(user);
        userRepository.save(user);
        log.info("User {} created", user.getEmail());
        return user;
    }

    @Override
    public void editUser(EditUserRequest request) {
        final var user = userRepository.findByMail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));
        canAccessUserPolicy.ensureCanEdit(user);
        user.setName(request.name());
        userRepository.save(user);
        log.info("User {} edited", user.getEmail());
    }

    @Override
    public User getUser(String userEmail) {
        final var user = userRepository.findByMail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
        canAccessUserPolicy.ensureCanRead(user);
        return user;
    }
}
