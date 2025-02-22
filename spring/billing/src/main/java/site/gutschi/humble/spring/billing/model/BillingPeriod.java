package site.gutschi.humble.spring.billing.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@RequiredArgsConstructor
//TODO: Document
public class BillingPeriod {
    private final LocalDate billingPeriodStart;
    private final LocalDate dueDate;
    private final LocalDate createdDate;
}
