package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InMemoryBillRepository implements BillRepository {
    private final Set<Bill> bills = new HashSet<>();
    private final LocalDate lastBillingPeriodStart = LocalDate.of(2021, 1, 1);
    private int nextId = 1;

    @Override
    public Set<Bill> findAllForPeriod(LocalDate start) {
        return bills.stream()
                .filter(bill -> bill.getBillingPeriodStart().equals(start))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Bill> findAllForCostCenter(CostCenter costCenter) {
        return bills.stream()
                .filter(bill -> bill.getCostCenter().equals(costCenter))
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Bill bill) {
        bills.removeIf(b -> b.getCostCenter().equals(bill.getCostCenter()) && b.getBillingPeriodStart().equals(bill.getBillingPeriodStart()));
        bills.add(bill);
    }

    @Override
    public int nextId() {
        final var result = nextId;
        nextId++;
        return result;
    }
}
