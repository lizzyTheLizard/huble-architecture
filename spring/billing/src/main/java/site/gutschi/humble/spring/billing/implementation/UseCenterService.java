package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.EditCostCenterUseCase;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

@RequiredArgsConstructor
@Service
@Slf4j
public class UseCenterService implements EditCostCenterUseCase {
    private final CanAccessPolicy canAccessPolicy;
    private final CostCenterRepository costCenterRepository;
    private final CurrentUserApi currentUserApi;
    private final GetProjectUseCase getProjectUseCase;

    @Override
    public void editCostCenter(EditCostCenterRequest request) {
        canAccessPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.currentEmail()));
        costCenter.setName(request.name());
        costCenter.setEmail(request.email());
        costCenter.setAddress(request.address());
        costCenterRepository.save(costCenter);
        log.info("Cost center {} updated by {}", costCenter.getId(), currentUserApi.currentEmail());
    }

    @Override
    public CostCenter createCostCenter(CreateCostCenterRequest request) {
        canAccessPolicy.ensureCanAccessBilling();
        final var id = costCenterRepository.getNextId();
        final var costCenter = CostCenter.create(id, request.name(), request.address(), request.email());
        costCenterRepository.save(costCenter);
        log.info("Cost center {} created by {}", costCenter.getId(), currentUserApi.currentEmail());
        return costCenter;
    }

    @Override
    public void deleteCostCenter(DeleteCostCenterRequest request) {
        canAccessPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.currentEmail()));
        costCenter.setActive(false);
        costCenterRepository.save(costCenter);
        log.info("Cost center {} deleted by {}", costCenter.getId(), currentUserApi.currentEmail());
    }

    @Override
    public void assignProjectToCostCenter(AssignCostCenterToUserRequest request) {
        canAccessPolicy.ensureCanAccessBilling();
        final var getProjectResponse = getProjectUseCase.getProject(request.projectKey());
        final var newCostCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.currentEmail()));
        final var oldCostCenter = costCenterRepository.findByProject(getProjectResponse.project());
        if (oldCostCenter.isPresent()) {
            oldCostCenter.get().removeProject(getProjectResponse.project());
            costCenterRepository.save(oldCostCenter.get());
        }
        newCostCenter.addProject(getProjectResponse.project());
        costCenterRepository.save(newCostCenter);
        log.info("Project {} assigned to cost center {} by {}", request.projectKey(), newCostCenter.getId(), currentUserApi.currentEmail());
    }

    @Override
    public void unassignProjectFromCostCenter(String projectKey) {
        canAccessPolicy.ensureCanAccessBilling();
        final var getProjectResponse = getProjectUseCase.getProject(projectKey);
        final var oldCostCenter = costCenterRepository.findByProject(getProjectResponse.project());
        if (oldCostCenter.isEmpty()) {
            return;
        }
        oldCostCenter.get().removeProject(getProjectResponse.project());
        costCenterRepository.save(oldCostCenter.get());
        log.info("Project {} removed from cost center {} by {}", projectKey, oldCostCenter.get().getId(), currentUserApi.currentEmail());
    }
}
