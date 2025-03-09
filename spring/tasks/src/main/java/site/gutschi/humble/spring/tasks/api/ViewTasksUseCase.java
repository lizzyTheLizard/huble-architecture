package site.gutschi.humble.spring.tasks.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public interface ViewTasksUseCase {
    /**
     * Get a task by its key
     * <p>
     * Checks if the task exists, if the user has access and return it if so.
     *
     * @throws NotFoundException If the task does not exist or is invisible.
     */
    GetTaskResponse getTaskByKey(TaskKey taskKey);

    /**
     * Find all tasks matching a search request
     */
    FindTasksResponse findTasks(FindTasksRequest request);

    /**
     * @param task      The task itself
     * @param editable  Is the task editable by the current user?
     * @param deletable Is the task deletable by the current user?
     * @param project   The project of the task
     */
    record GetTaskResponse(Task task, boolean editable, boolean deletable, Project project) {
    }

    /**
     * @param tasks    The matching tasks on this result page
     * @param projects All projects for those tasks
     * @param total    The total amount of results.
     */
    record FindTasksResponse(Collection<ViewTasksUseCase.TaskFindView> tasks, Collection<Project> projects, int total) {
    }

    /**
     * @param query    A search query
     * @param page     The result page, starting with 1
     * @param pageSize The size of one page
     */
    @Builder
    record FindTasksRequest(@NotNull String query, int page, int pageSize) {
    }

    /**
     * A view on a task in the search result
     *
     * @param key                 The key of the task
     * @param title               The title of the task
     * @param assigneeEmailOrNull The assignee-mail or null if not set
     * @param status              The status of the task
     */
    record TaskFindView(TaskKey key, String title, String assigneeEmailOrNull, TaskStatus status) {
    }
}
