package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.BillingPeriodEntity;

import java.util.Optional;

@Repository
public interface BillingPeriodEntityRepository extends JpaRepository<BillingPeriodEntity, Integer> {
    Optional<BillingPeriodEntity> findTopByOrderByStartDesc();
}
