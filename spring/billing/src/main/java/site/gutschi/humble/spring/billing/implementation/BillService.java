package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.ShowBillsUseCase;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class BillService implements ShowBillsUseCase {
    private final BillRepository billRepository;
    private final CanAccessBillingPolicy canAccessBillingPolicy;
    private final CostCenterRepository costCenterRepository;
    private final BillingPeriodRepository billingPeriodRepository;
    private final CurrentUserApi currentUserApi;


    @Override
    public Collection<BillingPeriod> getAllBillingPeriods() {
        canAccessBillingPolicy.ensureCanAccessBilling();
        return billingPeriodRepository.findAll();
    }

    @Override
    public Collection<Bill> getAllForPeriod(int billingPeriodId) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var billingPeriod = billingPeriodRepository.findById(billingPeriodId)
                .orElseThrow(() -> NotFoundException.notFound("BillingPeriod", String.valueOf(billingPeriodId), currentUserApi.getCurrentUser().getEmail()));
        return billRepository.findAllForPeriod(billingPeriod);
    }

    @Override
    public Collection<Bill> getAllForCostCenter(int costCenterId) {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var costCenter = costCenterRepository.findById(costCenterId)
                .orElseThrow(() -> NotFoundException.notFound("CostCenter", String.valueOf(costCenterId), currentUserApi.getCurrentUser().getEmail()));
        return billRepository.findAllForCostCenter(costCenter);
    }

    @Override
    public Collection<CostCenter> getAllCostCenters() {
        canAccessBillingPolicy.ensureCanAccessBilling();
        return costCenterRepository.findAll();
    }
}
