package site.gutschi.humble.spring.users.integration.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.domain.ports.UserRepository;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//TODO: Replace with a real database
public class UserRepositoryImplementation implements UserRepository {
    @Override
    public void save(User user) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Optional<User> findByMail(String email) {
        if (email.equals("test@example.com")) {
            final var user = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("{noop}password")
                    .systemAdmin(false)
                    .build();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getUserForProject(String projectKey) {
        if (projectKey.equals("PRO")) {
            final var user = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("{noop}password")
                    .systemAdmin(false)
                    .build();
            return List.of(user);
        }
        return List.of();
    }

}
