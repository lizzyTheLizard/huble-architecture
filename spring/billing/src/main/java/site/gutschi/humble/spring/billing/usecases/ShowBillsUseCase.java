package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.Set;

public interface ShowBillsUseCase {

    /**
     * Get all created billing periods so far
     */
    Set<BillingPeriod> getAllBillingPeriods();

    /**
     * Get all bills for a specific billing period
     */
    Set<Bill> getAllForPeriod(int billingPeriodId);

    /**
     * Get all bills for a specific cost center
     */
    Set<Bill> getAllForCostCenter(int costCenterId);

    /**
     * Get all cost centers
     */
    Set<CostCenter> getAllCostCenters();
}
