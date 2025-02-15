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
        final var entity = UserEntity.fromModel(user);
        userEntityRepository.save(entity);
    }

    @Override
    public Optional<User> findByMail(String email) {
        return userEntityRepository.findById(email)
                .map(UserEntity::toModel);
    }
}
