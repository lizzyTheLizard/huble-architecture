package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.*;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements CreateUserUseCase, EditUserUseCase, GetUserUseCase {
    private final UserRepository userRepository;
    private final AllowedToAccessPolicy allowedToAccessPolicy;
    private final KeyUniquePolicy keyUniquePolicy;

    @Override
    public User createUser(CreateUserRequest request) {
        keyUniquePolicy.ensureUserMailUnique(request.email());
        final var user = User.builder()
                .name(request.name())
                .email(request.email())
                .build();
        userRepository.save(user);
        log.info("User {} created", user.getEmail());
        return user;
    }

    @Override
    public void editUser(EditUserRequest request) {
        final var user = userRepository.findByMail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));
        allowedToAccessPolicy.ensureCanEdit(user);
        user.setName(request.name());
        userRepository.save(user);
        log.info("User {} edited", user.getEmail());
    }

    @Override
    public Optional<User> getUser(String userEmail) {
        return userRepository.findByMail(userEmail)
                .filter(allowedToAccessPolicy::canRead);
    }
}
