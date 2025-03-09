package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.util.Collection;

public interface BillRepository {
    /**
     * Returns all bills for a given period.
     */
    Collection<Bill> findAllForPeriod(BillingPeriod billingPeriodId);

    /**
     * Returns all bills for a given cost center.
     */
    Collection<Bill> findAllForCostCenter(CostCenter costCenterId);

    /**
     * Saves a new bill and returns the saved bill.
     */
    void save(Bill bill);
}
