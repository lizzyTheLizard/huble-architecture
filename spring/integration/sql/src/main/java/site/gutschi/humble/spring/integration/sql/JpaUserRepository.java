package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.UserEntity;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final UserEntityRepository userEntityRepository;

    @Override
    public void save(User user) {
        final var entity = fromModel(user);
        userEntityRepository.save(entity);
    }

    @Override
    public Optional<User> findByMail(String email) {
        return userEntityRepository.findById(email)
                .map(this::toModel);
    }

    public UserEntity fromModel(User user) {
        final var entity = new UserEntity();
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        return entity;
    }

    public User toModel(UserEntity entity) {
        return User.builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .build();
    }

}
