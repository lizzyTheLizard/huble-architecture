package site.gutschi.humble.spring.tasks.domain.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.error.NotFoundException;
import site.gutschi.humble.spring.tasks.domain.api.*;
import site.gutschi.humble.spring.tasks.domain.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.domain.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;


import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
class TaskService implements EditTaskUseCase, GetTasksUseCase, CreateTaskUseCase {
    private final UserApi userApi;
    private final TimeApi timeApi;
    private final GetProjectApi getProjectApi;
    private final TaskRepository taskRepository;
    private final CanAccessPolicy canAccessPolicy;
    private final ProjectActivePolicy projectActivePolicy;
    private final NotDeletedPolicy notDeletedPolicy;
    private final SearchCaller searchCaller;

    @Override
    public void edit(EditTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessPolicy.ensureCanEditTasksInProject(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setEstimation(request.estimation());
        existingTask.setAssigneeEmail(request.assignee());
        existingTask.setStatus(request.status());
        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} edited", existingTask.getKey());
    }

    @Override
    public void comment(CommentTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.addComment(request.comment());
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} commented", existingTask.getKey());
    }

    @Override
    public void delete(DeleteTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> NotFoundException.taskNotFound(request.taskKey()));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessPolicy.ensureCanDeleteTasksInProject(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setDeleted();
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} deleted", existingTask.getKey());
    }

    @Override
    public GetTaskResponse getTaskByKey(String taskKey) {
        final var existingTask = taskRepository.findByKey(taskKey)
                .orElseThrow(() -> NotFoundException.taskNotFound(taskKey));
        final var project = getProjectApi.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(existingTask.getProjectKey()));
        notDeletedPolicy.ensureNotDeleted(existingTask);
        final var editable = canAccessPolicy.canEditTasksInProject(project);
        final var deletable = canAccessPolicy.canDeleteTasksInProject(project);
        return new GetTaskResponse(existingTask, editable, deletable, project);
    }

    @Override
    public FindTasksResponse findTasks(FindTasksRequest request) {
        final var projects = getProjectApi.getAllProjects();
        final var searchCallerRequest = new SearchCallerRequest(request.query(), request.page(), request.pageSize(), projects);
        final var searchCallerResponse = searchCaller.findTasks(searchCallerRequest);
        return new FindTasksResponse(searchCallerResponse.tasks(), projects, searchCallerResponse.total());
    }

    @Override
    public Task create(CreateTaskRequest request) {
        final var project = getProjectApi.getProject(request.projectKey())
                .orElseThrow(() -> NotFoundException.projectNotFound(request.projectKey()));
        canAccessPolicy.ensureCanEditTasksInProject(project);
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
        searchCaller.informUpdatedTasks(task);
        log.info("Task {} created", task.getKey());
        return task;
    }

    @Override
    public Collection<Project> getProjectsToCreate() {
        return getProjectApi.getAllProjects();
    }
}
