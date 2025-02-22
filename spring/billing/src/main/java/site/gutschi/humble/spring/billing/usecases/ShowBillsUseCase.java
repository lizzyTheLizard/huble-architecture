package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.Set;

//TODO BILLING: Document and Test ShowBillsUseCase
public interface ShowBillsUseCase {
    Set<BillingPeriod> getAllBillingPeriods();

    Set<Bill> getAllForPeriod(int billingPeriodId);

    Set<Bill> getAllForCostCenter(int costCenterId);

    Set<CostCenter> getAllCostCenters();
}
