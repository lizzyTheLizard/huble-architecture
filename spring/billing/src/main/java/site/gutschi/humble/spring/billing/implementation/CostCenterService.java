package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.EditCostCenterUseCase;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;

@RequiredArgsConstructor
@Service
@Slf4j
public class CostCenterService implements EditCostCenterUseCase {
    private final CanAccessBillingPolicy canAccessBillingPolicy;
    private final CostCenterRepository costCenterRepository;
    private final CurrentUserApi currentUserApi;
    private final CostCenterValidPolicy costCenterValidPolicy;
    private final GetProjectApi getProjectApi;

    @Override
    public void editCostCenter(EditCostCenterRequest request) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.getCurrentUser().getEmail()));
        costCenter.setName(request.name());
        costCenter.setEmail(request.email());
        costCenter.setAddress(request.address());
        costCenterValidPolicy.ensureCostCenterValid(costCenter);
        costCenterRepository.save(costCenter);
        log.info("Cost center {} updated", costCenter.getId());
    }

    @Override
    public CostCenter createCostCenter(CreateCostCenterRequest request) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var costCenter = CostCenter.create(request.name(), request.address(), request.email());
        costCenterValidPolicy.ensureCostCenterValid(costCenter);
        final var persistedCostCenter = costCenterRepository.save(costCenter);
        log.info("Cost center {} created", persistedCostCenter.getId());
        return persistedCostCenter;
    }

    @Override
    public void deleteCostCenter(DeleteCostCenterRequest request) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.getCurrentUser().getEmail()));
        costCenter.setDeleted(true);
        costCenterRepository.save(costCenter);
        log.info("Cost center {} deleted", costCenter.getId());
    }

    @Override
    public void assignProjectToCostCenter(AssignCostCenterToUserRequest request) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var project = getProjectApi.getProject(request.projectKey());
        final var newCostCenter = costCenterRepository.findById(request.costCenterId())
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(request.costCenterId()), currentUserApi.getCurrentUser().getEmail()));
        final var oldCostCenter = costCenterRepository.findByProject(project);
        if (oldCostCenter.isPresent()) {
            oldCostCenter.get().removeProject(project);
            costCenterRepository.save(oldCostCenter.get());
        }
        newCostCenter.addProject(project);
        costCenterRepository.save(newCostCenter);
        log.info("Project {} assigned to cost center {}", request.projectKey(), newCostCenter.getId());
    }

    @Override
    public void unassignProjectFromCostCenter(String projectKey) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var project = getProjectApi.getProject(projectKey);
        final var oldCostCenter = costCenterRepository.findByProject(project);
        if (oldCostCenter.isEmpty()) {
            return;
        }
        oldCostCenter.get().removeProject(project);
        costCenterRepository.save(oldCostCenter.get());
        log.info("Project {} removed from cost center {}", projectKey, oldCostCenter.get().getId());
    }
}
