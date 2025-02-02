package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsRequest;
import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsUseCase;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationCaller;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;

@RequiredArgsConstructor
@Slf4j
@Service
class ImplementationService implements UpdateImplementationsUseCase {
    private final CheckImplementationCaller checkImplementationCaller;
    private final TaskRepository taskRepository;

    @Override
    public void updateImplementations(UpdateImplementationsRequest request) {
        final var response = checkImplementationCaller.checkForImplementations(request);
        for (var implementations : response.entries()) {
            //Errors in one tasks are logged but do not cause the whole process to fail
            try {
                final var existingTask = taskRepository.findByKey(implementations.taskKey())
                        .orElseThrow(() -> NotFoundException.taskNotFound(implementations.taskKey()));
                existingTask.addImplementation(implementations.url(), implementations.description());
                taskRepository.save(existingTask);
            } catch (Exception e) {
                log.error("Error while saving implementation for task {}", implementations.taskKey(), e);
            }
        }
    }

}
