package site.gutschi.humble.spring.billing.usecases;

import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;

import java.time.LocalDate;

public interface UpdateBillsUseCase {
    /**
     * Generate all new Bills for a given billing period
     *
     * @throws NotAllowedException   If you are not allowed to manage bookings
     * @throws InvalidInputException If the billing period is invalid (in the future, already billed, not start of month)
     */
    void updateBills(LocalDate billingPeriodStart);
}
