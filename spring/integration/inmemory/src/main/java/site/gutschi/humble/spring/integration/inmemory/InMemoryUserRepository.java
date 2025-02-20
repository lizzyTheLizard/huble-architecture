package site.gutschi.humble.spring.integration.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Set<User> users = new HashSet<>();

    @Override
    public void save(User user) {
        users.removeIf(u -> u.getEmail().equals(user.getEmail()));
        users.add(user);
    }

    @Override
    public Optional<User> findByMail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }
}
