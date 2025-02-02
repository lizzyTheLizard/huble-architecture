package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.*;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class TaskService implements EditTaskUseCase, CommentTaskUseCase, DeleteTaskUseCase, GetTasksUseCase, CreateTaskUseCase {
    private final UserApi userApi;
    private final TimeApi timeApi;
    private final GetProjectApi getProjectApi;
    private final TaskRepository taskRepository;
    private final CanAccessPolicy canAccessPolicy;
    private final ProjectActivePolicy projectActivePolicy;
    private final NotDeletedPolicy notDeletedPolicy;

    @Override
    public void edit(EditTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessPolicy.ensureWriteAccess(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setEstimation(request.estimation());
        existingTask.setAssigneeEmail(request.assignee());
        existingTask.setStatus(request.status());
        existingTask.setTitle(request.title());
        existingTask.setAdditionalFields(request.additionalFields());
        existingTask.setDescription(request.description());
        taskRepository.save(existingTask);
    }

    @Override
    public void comment(CommentTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessPolicy.ensureReadAccess(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.addComment(request.comment());
        taskRepository.save(existingTask);
    }

    @Override
    public void delete(DeleteTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessPolicy.ensureDeleteAccess(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setDeleted();
        taskRepository.save(existingTask);
    }

    @Override
    public GetTasksResponse getTaskByKey(String taskKey) {
        final var existingTask = taskRepository.findByKey(taskKey)
                .orElseThrow(() -> NotFoundException.taskNotFound(taskKey));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        canAccessPolicy.ensureReadAccess(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        final var editable = canAccessPolicy.canRead(project);
        final var deletable = canAccessPolicy.canDelete(project);
        final var projectUsers = project.getProjectRoles()
                .stream()
                .collect(Collectors.toMap(r -> r.user().getEmail(), ProjectRole::user));
        return new GetTasksResponse(existingTask, editable, deletable, project, projectUsers);
    }

    @Override
    public Collection<Task> getTasks(GetTasksRequest request) {
        final var tasks = taskRepository.findTasks(request);
        final var projects = tasks.stream()
                .map(Task::getProjectKey)
                .distinct()
                .flatMap(t -> getProjectApi.getProject(t).stream())
                .collect(Collectors.toMap(Project::getKey, p -> p));
        return tasks.stream()
                .filter(t -> {
                    final var project = projects.get(t.getProjectKey());
                    if (project == null) return false;
                    return canAccessPolicy.canRead(project);
                })
                .collect(Collectors.toList());
    }

    @Override
    public int count(GetTasksRequest request) {
        final var tasks = taskRepository.findTasksWithoutPaging(request);
        final var projects = tasks.stream()
                .map(Task::getProjectKey)
                .distinct()
                .flatMap(t -> getProjectApi.getProject(t).stream())
                .collect(Collectors.toMap(Project::getKey, p -> p));
        return (int) tasks.stream()
                .filter(t -> {
                    final var project = projects.get(t.getProjectKey());
                    if (project == null) return false;
                    return canAccessPolicy.canRead(project);
                })
                .count();
    }

    @Override
    public Task create(CreateTaskRequest request) {
        final var project = getProjectApi.getProject(request.projectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(request.projectKey()));
        canAccessPolicy.ensureWriteAccess(project);
        projectActivePolicy.ensureProjectIsActive(project);
        final var task = Task.builder()
                .id(taskRepository.nextId(request.projectKey()))
                .projectKey(request.projectKey())
                .creatorEmail(userApi.currentEmail())
                .status(TaskStatus.FUNNEL)
                .title(request.title())
                .description(request.description())
                .timeApi(timeApi)
                .userApi(userApi)
                .build();
        taskRepository.save(task);
        return task;
    }
}
