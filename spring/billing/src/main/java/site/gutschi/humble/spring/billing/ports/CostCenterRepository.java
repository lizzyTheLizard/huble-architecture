package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;

//TODO: Document
public interface CostCenterRepository {
    Optional<CostCenter> findById(int id);

    void save(CostCenter costCenter);

    Set<CostCenter> findAll();

    int getNextId();

    Optional<CostCenter> findByProject(Project project);
}
