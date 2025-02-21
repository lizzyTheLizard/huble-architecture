package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;

@Service
@RequiredArgsConstructor
public final class NotDeletedPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureNotDeleted(Task task) {
        if (task.isDeleted()) {
            throw NotFoundException.deleted("Task", task.getKey().toString(), currentUserApi.currentEmail());
        }
    }
}
