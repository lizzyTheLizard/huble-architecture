package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.ShowBillsUseCase;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BillService implements ShowBillsUseCase {
    private final BillRepository billRepository;
    private final CanAccessPolicy canAccessPolicy;
    private final CostCenterRepository costCenterRepository;
    private final CurrentUserApi currentUserApi;

    @Override
    public Set<Bill> getAllForPeriod(LocalDate start) {
        canAccessPolicy.ensureCanAccessBilling();
        return billRepository.findAllForPeriod(start);
    }

    @Override
    public Set<Bill> getAllForCostCenter(int costCenterId) {
        canAccessPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(costCenterId)
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(costCenterId), currentUserApi.currentEmail()));
        return billRepository.findAllForCostCenter(costCenter);
    }

    @Override
    public Set<CostCenter> getAllCostCenters() {
        canAccessPolicy.ensureCanAccessBilling();
        return costCenterRepository.findAll();
    }
}
