package site.gutschi.humble.spring.integration.sql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.gutschi.humble.spring.integration.sql.entity.BillEntity;
import site.gutschi.humble.spring.integration.sql.entity.CostCenterEntity;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BillEntityRepository extends JpaRepository<BillEntity, Integer> {
    Set<BillEntity> findByCostCenter(CostCenterEntity costCenterEntity);

    Set<BillEntity> findByBillingPeriodStart(LocalDate billingPeriodStart);
}
