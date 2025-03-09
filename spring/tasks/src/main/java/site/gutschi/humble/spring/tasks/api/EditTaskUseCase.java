package site.gutschi.humble.spring.tasks.api;

import lombok.Builder;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

public interface EditTaskUseCase {

    /**
     * Edit an existing task
     * <p>
     * Checks if the task exists, the user is allowed to edit tasks in this project, edits the task and save it.
     *
     * @throws NotAllowedException   If the user is not allowed to edit this task
     * @throws NotFoundException     If the task or the assigned user does not exist or is invisible.
     * @throws InvalidInputException If the input is not valid.
     */
    void edit(EditTaskRequest request);

    /**
     * Comments an existing task
     * <p>
     * Checks if the task exists, the user is allowed to comment tasks in this project, add the comment and save the task.
     *
     * @throws NotAllowedException   If the user is not allowed to comment this task
     * @throws NotFoundException     If the task does not exist or is invisible.
     * @throws InvalidInputException If the input is not valid.
     */
    void comment(CommentTaskRequest request);

    /**
     * Delete an existing task
     * <p>
     * Checks if the task exists, the user is allowed to delete tasks in this project and deletes the task.
     *
     * @throws NotAllowedException If the user is not allowed to delete this task
     * @throws NotFoundException   If the task does not exist or is invisible.
     */
    void delete(TaskKey taskKey);

    /**
     * @param taskKey The key of the task to comment
     * @param comment The text of the comment to add
     */
    record CommentTaskRequest(TaskKey taskKey, String comment) {
    }

    /**
     * @param taskKey     The key of the task to edit
     * @param title       The new title
     * @param description The new description
     * @param status      The new status
     * @param assignee    The new assignee
     * @param estimation  The new estimation
     */
    @Builder
    record EditTaskRequest(TaskKey taskKey, String title, String description, TaskStatus status, String assignee,
                           Integer estimation) {
    }
}

