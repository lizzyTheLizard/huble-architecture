package site.gutschi.humble.spring.users.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.domain.api.*;
import site.gutschi.humble.spring.users.domain.ports.UserRepository;
import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageUserUseCase implements CreateUserApi, EditUserApi, GetUserApi {
    private final UserRepository userRepository;
    private final AllowedToAccessPolicy allowedToAccessPolicy;

    @Override
    public User createUser(CreateUserRequest request) {
        final var existing = userRepository.findByMail(request.email());
        if (existing.isPresent())
            throw NotUniqueException.emailAlreadyExists(request.email());
        final var user = User.builder()
                .name(request.name())
                .email(request.email())
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public void editUser(EditUserRequest request) {
        final var user = userRepository.findByMail(request.email())
                .orElseThrow(() -> NotFoundException.userNotFound(request.email()));
        allowedToAccessPolicy.ensureCanEdit(user);
        user.setName(request.name());
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(String userEmail) {
        return userRepository.findByMail(userEmail);
    }
}
