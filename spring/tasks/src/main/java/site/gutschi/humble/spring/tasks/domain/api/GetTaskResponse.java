package site.gutschi.humble.spring.tasks.domain.api;

import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.Map;

public record GetTaskResponse(Task task, boolean editable, boolean deletable, Project project) {
}
