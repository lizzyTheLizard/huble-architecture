package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.CostCenterEntity;
import site.gutschi.humble.spring.integration.sql.entity.ProjectEntity;

import java.util.Optional;

@Repository
public interface CostCenterEntityRepository extends JpaRepository<CostCenterEntity, Integer> {
    Optional<CostCenterEntity> findByProjects(ProjectEntity project);
}
