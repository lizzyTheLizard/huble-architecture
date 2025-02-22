package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.CostCenter;

import java.time.LocalDate;
import java.util.Set;

//TODO: Document
//TODO: Test
public interface ShowBillsUseCase {
    Set<Bill> getAllForPeriod(LocalDate start);

    Set<Bill> getAllForCostCenter(int costCenterId);

    Set<CostCenter> getAllCostCenters();
}
