package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;

import java.util.List;

public interface EditCostCenterUseCase {
    /**
     * Edit an existing cost center
     *
     * @throws NotAllowedException   If you are not allowed to manage bookings
     * @throws InvalidInputException If the inputs are not valid
     * @throws NotFoundException     If the cost center does not exist
     */
    void editCostCenter(EditCostCenterRequest request);

    /**
     * Create a new cost center and return the created cost center.
     *
     * @throws NotAllowedException   If you are not allowed to manage bookings
     * @throws InvalidInputException If the inputs are not valid
     */
    CostCenter createCostCenter(CreateCostCenterRequest request);

    /**
     * Delete a cost center
     *
     * @throws NotAllowedException If you are not allowed to manage bookings
     * @throws NotFoundException   If the cost center does not exist
     */
    void deleteCostCenter(DeleteCostCenterRequest request);

    /**
     * Assign a project to a cost center
     *
     * @throws NotAllowedException If you are not allowed to manage bookings
     * @throws NotFoundException   If the cost center or project does not exist
     */
    void assignProjectToCostCenter(AssignCostCenterToUserRequest request);

    /**
     * Unassign a project from a cost center
     *
     * @throws NotAllowedException If you are not allowed to manage bookings
     * @throws NotFoundException   If the project does not exist
     */
    void unassignProjectFromCostCenter(String projectKey);

    /**
     * @param costCenterId The ID of the cost center
     * @param address      The new address of the cost center
     * @param name         The new name of the cost center
     * @param email        The new email of the cost center
     */
    record EditCostCenterRequest(int costCenterId, List<String> address, String name, String email) {
    }

    /**
     * @param address The address of the cost center
     * @param name    The name of the cost center
     * @param email   The email of the cost center
     */
    record CreateCostCenterRequest(List<String> address, String name, String email) {
    }

    /**
     * @param costCenterId The ID of the cost center to delete
     */
    record DeleteCostCenterRequest(int costCenterId) {
    }

    /**
     * @param costCenterId The ID of the cost center
     * @param projectKey   The key of the project
     */
    record AssignCostCenterToUserRequest(int costCenterId, String projectKey) {
    }
}

