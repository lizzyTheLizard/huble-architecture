package site.gutschi.humble.spring.tasks.api;

import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;

public record GetTaskResponse(Task task, boolean editable, boolean deletable, Project project) {
}
