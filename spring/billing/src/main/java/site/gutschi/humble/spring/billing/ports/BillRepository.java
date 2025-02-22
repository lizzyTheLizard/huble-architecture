package site.gutschi.humble.spring.billing.ports;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.time.LocalDate;
import java.util.Set;

//TODO: Document
public interface BillRepository {
    Set<Bill> findAllForPeriod(LocalDate start);

    Set<Bill> findAllForCostCenter(CostCenter costCenter);
    
    void save(Bill bill);

    int nextId();
}
