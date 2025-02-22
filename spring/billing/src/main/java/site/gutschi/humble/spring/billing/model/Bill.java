package site.gutschi.humble.spring.billing.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@RequiredArgsConstructor
//TODO: Document
public class Bill {
    private final int id;
    private final CostCenter costCenter;
    private final LocalDate billingPeriodStart;
    private final LocalDate dueDate;
    private final LocalDate createdDate;
    private final Set<ProjectBill> projectBills;
}
