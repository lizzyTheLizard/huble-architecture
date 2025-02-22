package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.ports.BillRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InMemoryBillRepository implements BillRepository {
    private final Set<Bill> bills = new HashSet<>();
    private int nextId = 1;

    @Override
    public Set<Bill> findAllForPeriod(int billingPeriodId) {
        return bills.stream()
                .filter(bill -> bill.billingPeriod().id().equals(billingPeriodId))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Bill> findAllForCostCenter(int costCenterId) {
        return bills.stream()
                .filter(bill -> bill.costCenter().getId().equals(costCenterId))
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Bill bill) {
        if (bill.id() != null)
            bills.removeIf(b -> b.id().equals(bill.id()));
        final var result = new Bill(
                bill.id() != null ? bill.id() : nextId++,
                bill.costCenter(),
                bill.billingPeriod(),
                bill.projectBills()
        );
        bills.add(result);
    }
}
