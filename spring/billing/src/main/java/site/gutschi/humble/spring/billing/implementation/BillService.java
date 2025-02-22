package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.ShowBillsUseCase;
import site.gutschi.humble.spring.common.api.CurrentUserApi;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class BillService implements ShowBillsUseCase {
    private final BillRepository billRepository;
    private final CanAccessBillingPolicy canAccessBillingPolicy;
    private final CostCenterRepository costCenterRepository;
    private final CurrentUserApi currentUserApi;


    @Override
    public Set<BillingPeriod> getAllBillingPeriods() {
        return Set.of();
    }

    @Override
    public Set<Bill> getAllForPeriod(int billingPeriodId) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        return billRepository.findAllForPeriod(billingPeriodId);
    }

    @Override
    public Set<Bill> getAllForCostCenter(int costCenterId) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        return billRepository.findAllForCostCenter(costCenterId);
    }

    @Override
    public Set<CostCenter> getAllCostCenters() {
        canAccessBillingPolicy.ensureCanAccessBilling();
        return costCenterRepository.findAll();
    }
}
