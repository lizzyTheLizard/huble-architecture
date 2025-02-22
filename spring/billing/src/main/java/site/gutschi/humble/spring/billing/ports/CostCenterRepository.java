package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;

public interface CostCenterRepository {
    /**
     * Returns the cost center with the given ID or empty if no cost center exists.
     */
    Optional<CostCenter> findById(int id);

    /**
     * Saves a new cost center and returns the saved cost center.
     */
    //TODO BILLING: Use return value
    CostCenter save(CostCenter costCenter);

    /**
     * Returns all cost centers.
     */
    Set<CostCenter> findAll();

    /**
     * Returns the cost center for the given project or empty if no cost center exists.
     */
    Optional<CostCenter> findByProject(Project project);
}
