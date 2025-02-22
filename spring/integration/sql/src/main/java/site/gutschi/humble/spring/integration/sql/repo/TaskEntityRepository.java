package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.ProjectEntity;
import site.gutschi.humble.spring.integration.sql.entity.TaskEntity;

import java.util.Set;

@Repository
public interface TaskEntityRepository extends JpaRepository<TaskEntity, String> {
    Set<TaskEntity> findByProject(ProjectEntity project);
}
