package site.gutschi.humble.spring.tasks.api;

import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public record FindTasksResponse(Collection<TaskFindView> tasks, Collection<Project> projects, int total) {
    public record TaskFindView(String key, String title, String assigneeEmailOrNull, TaskStatus status) {
    }
}
