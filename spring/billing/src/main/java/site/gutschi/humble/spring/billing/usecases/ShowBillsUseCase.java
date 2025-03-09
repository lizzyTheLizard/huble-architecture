package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.Collection;

public interface ShowBillsUseCase {

    /**
     * Get all created billing periods so far
     */
    Collection<BillingPeriod> getAllBillingPeriods();

    /**
     * Get all bills for a specific billing period
     */
    Collection<Bill> getAllForPeriod(int billingPeriodId);

    /**
     * Get all bills for a specific cost center
     */
    Collection<Bill> getAllForCostCenter(int costCenterId);

    /**
     * Get all cost centers
     */
    Collection<CostCenter> getAllCostCenters();
}
