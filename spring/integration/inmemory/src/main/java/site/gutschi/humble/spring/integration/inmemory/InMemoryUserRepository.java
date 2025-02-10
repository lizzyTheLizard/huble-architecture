package site.gutschi.humble.spring.integration.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Collection<User> users = new LinkedList<>();

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
