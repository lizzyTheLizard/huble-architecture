package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.api.*;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.api.ProjectNotFoundException;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService implements EditTaskUseCase, GetTasksUseCase, CreateTaskUseCase {
    private final CurrentUserApi currentUserApi;
    private final GetProjectUseCase getProjectUseCase;
    private final TaskRepository taskRepository;
    private final CanAccessPolicy canAccessPolicy;
    private final ProjectActivePolicy projectActivePolicy;
    private final NotDeletedPolicy notDeletedPolicy;
    private final SearchCaller searchCaller;

    @Override
    public void edit(EditTaskRequest request) {
        final var existingTask = taskRepository.findByKey(request.taskKey())
                .orElseThrow(() -> new TaskNotFoundException(request.taskKey()));
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> new ProjectNotFoundException(existingTask.getProjectKey()));
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
                .orElseThrow(() -> new TaskNotFoundException(request.taskKey()));
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> new ProjectNotFoundException(existingTask.getProjectKey()));
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
                .orElseThrow(() -> new TaskNotFoundException(request.taskKey()));
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> new ProjectNotFoundException(existingTask.getProjectKey()));
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
                .orElseThrow(() -> new TaskNotFoundException(taskKey));
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey())
                .orElseThrow(() -> new ProjectNotFoundException(existingTask.getProjectKey()));
        notDeletedPolicy.ensureNotDeleted(existingTask);
        final var editable = canAccessPolicy.canEditTasksInProject(project);
        final var deletable = canAccessPolicy.canDeleteTasksInProject(project);
        return new GetTaskResponse(existingTask, editable, deletable, project);
    }

    @Override
    public FindTasksResponse findTasks(FindTasksRequest request) {
        final var projects = getProjectUseCase.getAllProjects();
        final var searchCallerRequest = new SearchCallerRequest(request.query(), request.page(), request.pageSize(), projects);
        final var searchCallerResponse = searchCaller.findTasks(searchCallerRequest);
        return new FindTasksResponse(searchCallerResponse.tasks(), projects, searchCallerResponse.total());
    }

    @Override
    public Task create(CreateTaskRequest request) {
        final var project = getProjectUseCase.getProject(request.projectKey())
                .orElseThrow(() -> new ProjectNotFoundException(request.projectKey()));
        canAccessPolicy.ensureCanEditTasksInProject(project);
        projectActivePolicy.ensureProjectIsActive(project);
        final var task = Task.builder()
                .id(taskRepository.nextId(request.projectKey()))
                .projectKey(request.projectKey())
                .creatorEmail(currentUserApi.currentEmail())
                .status(TaskStatus.FUNNEL)
                .title(request.title())
                .description(request.description())
                .currentUserApi(currentUserApi)
                .build();
        taskRepository.save(task);
        searchCaller.informUpdatedTasks(task);
        log.info("Task {} created", task.getKey());
        return task;
    }

    @Override
    public Collection<Project> getProjectsToCreate() {
        return getProjectUseCase.getAllProjects();
    }
}
