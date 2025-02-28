package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.Bill;

import java.util.Set;

public interface BillRepository {
    /**
     * Returns all bills for a given period.
     */
    //TODO Modelling: Use Billing Period instead of int
    Set<Bill> findAllForPeriod(int billingPeriodId);

    /**
     * Returns all bills for a given cost center.
     */
    //TODO Modelling: Use CostCenter instead of int
    Set<Bill> findAllForCostCenter(int costCenterId);

    /**
     * Saves a new bill and returns the saved bill.
     */
    void save(Bill bill);
}
