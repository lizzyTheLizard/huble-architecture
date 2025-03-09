package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;

@Service
@RequiredArgsConstructor
public class CanAccessBillingPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanAccessBilling() {
        if (currentUserApi.isSystemAdmin()) return;
        throw NotAllowedException.notAllowed("Billing", "access", currentUserApi.getCurrentUser().getEmail());
    }
}
