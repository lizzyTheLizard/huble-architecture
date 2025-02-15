package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.ProjectEntity;
import site.gutschi.humble.spring.integration.sql.entity.UserEntity;

import java.util.Collection;

@Repository
public interface ProjectEntityRepository extends JpaRepository<ProjectEntity, String> {
    @Query("SELECT p FROM project p, projectRole pr where pr member of p.projectRoles and pr.user = ?1")
    Collection<ProjectEntity> findByUser(UserEntity user);
}
