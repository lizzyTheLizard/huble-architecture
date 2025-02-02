package site.gutschi.humble.spring.tasks.integration.github;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsRequest;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationCaller;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationsResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
//TODO: Implement real check implementation caller
public class CheckImplementationCallerImplementation implements CheckImplementationCaller {

    @Override
    public CheckImplementationsResponse checkForImplementations(UpdateImplementationsRequest request) {
        return new CheckImplementationsResponse(List.of());
    }
}
