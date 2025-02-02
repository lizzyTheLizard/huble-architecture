package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsRequest;

public interface CheckImplementationCaller {
    CheckImplementationsResponse checkForImplementations(UpdateImplementationsRequest request);
}
