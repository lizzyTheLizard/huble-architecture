package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.api.CurrentUserApi;

@Service
@RequiredArgsConstructor
public final class NotDeletedPolicy {
    private final CurrentUserApi currentUserApi;

    public void ensureNotDeleted(Task task) {
        if (!task.isDeleted()) return;
        final var currentUser = currentUserApi.getCurrentUser().getEmail();
        throw NotFoundException.deleted("Task", task.getKey().toString(), currentUser);
    }
}
