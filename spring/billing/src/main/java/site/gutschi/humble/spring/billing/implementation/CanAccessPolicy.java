package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;

@Service
@RequiredArgsConstructor
public class CanAccessPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureCanAccessBilling() {
        if (currentUserApi.isSystemAdmin()) return;
        throw NotAllowedException.notAllowed("Billing", "access", currentUserApi.currentEmail());
    }
}
