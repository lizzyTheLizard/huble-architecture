package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.List;

//TODO BILLING: Document and Test EditCostCenterUseCase
public interface EditCostCenterUseCase {
    void editCostCenter(EditCostCenterRequest request);

    CostCenter createCostCenter(CreateCostCenterRequest request);

    void deleteCostCenter(DeleteCostCenterRequest request);

    void assignProjectToCostCenter(AssignCostCenterToUserRequest request);

    void unassignProjectFromCostCenter(String projectKey);

    record EditCostCenterRequest(int costCenterId, List<String> address, String name, String email) {
    }

    record CreateCostCenterRequest(List<String> address, String name, String email) {
    }

    record DeleteCostCenterRequest(int costCenterId) {
    }

    record AssignCostCenterToUserRequest(int costCenterId, String projectKey) {
    }
}

