package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.UserEntity;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
}
