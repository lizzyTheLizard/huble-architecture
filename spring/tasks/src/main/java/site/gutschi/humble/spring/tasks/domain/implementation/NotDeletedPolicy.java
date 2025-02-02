package site.gutschi.humble.spring.tasks.domain.implementation;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.tasks.model.Task;

@Service
public final class NotDeletedPolicy {

    public void ensureNotDeleted(Task task) {
        if (task.isDeleted()) {
            throw new NotAllowedException("You are not allowed to access deleted task '" + task.getKey() + "'");
        }
    }
}
