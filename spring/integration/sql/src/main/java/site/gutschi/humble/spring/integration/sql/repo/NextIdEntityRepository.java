package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;

@Repository
public interface NextIdEntityRepository extends JpaRepository<NextIdEntity, String> {
}
