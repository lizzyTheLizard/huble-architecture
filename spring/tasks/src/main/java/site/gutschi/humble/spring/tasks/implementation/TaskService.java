package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.usecases.CreateTaskUseCase;
import site.gutschi.humble.spring.tasks.usecases.EditTaskUseCase;
import site.gutschi.humble.spring.tasks.usecases.GetTasksUseCase;
import site.gutschi.humble.spring.tasks.usecases.ViewTasksUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService implements EditTaskUseCase, ViewTasksUseCase, GetTasksUseCase, CreateTaskUseCase {
    private final CurrentUserApi currentUserApi;
    private final GetProjectUseCase getProjectUseCase;
    private final TaskRepository taskRepository;
    private final CanAccessTasksPolicy canAccessTasksPolicy;
    private final ProjectActivePolicy projectActivePolicy;
    private final NotDeletedPolicy notDeletedPolicy;
    private final SearchCaller searchCaller;

    @Override
    public void edit(EditTaskRequest request) {
        final var existingTask = getTaskInternal(request.taskKey());
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey());
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessTasksPolicy.ensureCanEditTasksInProject(project);
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
        final var existingTask = getTaskInternal(request.taskKey());
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey());
        projectActivePolicy.ensureProjectIsActive(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.addComment(request.comment());
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} commented", existingTask.getKey());
    }

    @Override
    public void delete(TaskKey taskKey) {
        final var existingTask = getTaskInternal(taskKey);
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey());
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessTasksPolicy.ensureCanDeleteTasksInProject(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setDeleted();
        taskRepository.save(existingTask);
        searchCaller.informDeletedTasks(existingTask);
        log.info("Task {} deleted", existingTask.getKey());
    }

    @Override
    public GetTaskResponse getTaskByKey(TaskKey taskKey) {
        final var existingTask = getTaskInternal(taskKey);
        final var project = getProjectUseCase.getProject(existingTask.getProjectKey());
        notDeletedPolicy.ensureNotDeleted(existingTask);
        final var editable = canAccessTasksPolicy.canEditTasksInProject(project);
        final var deletable = canAccessTasksPolicy.canDeleteTasksInProject(project);
        return new GetTaskResponse(existingTask, editable, deletable, project);
    }

    @Override
    public FindTasksResponse findTasks(FindTasksRequest request) {
        final var projects = getProjectUseCase.getAllProjects();
        final var searchCallerRequest = new SearchCaller.SearchCallerRequest(request.query(), request.page(), request.pageSize(), projects);
        final var searchCallerResponse = searchCaller.findTasks(searchCallerRequest);
        return new FindTasksResponse(searchCallerResponse.tasks(), projects, searchCallerResponse.total());
    }

    @Override
    public Set<Task> getTasksForProject(String projectKey) {
        final var project = getProjectUseCase.getProject(projectKey);
        return taskRepository.findByProject(project);
    }

    @Override
    public Task create(CreateTaskRequest request) {
        final var project = getProjectUseCase.getProject(request.projectKey());
        canAccessTasksPolicy.ensureCanEditTasksInProject(project);
        projectActivePolicy.ensureProjectIsActive(project);
        final var nextId = taskRepository.nextId(request.projectKey());
        final var task = Task.createNew(currentUserApi, request.projectKey(), nextId, request.title(), request.description());
        taskRepository.save(task);
        searchCaller.informUpdatedTasks(task);
        log.info("Task {} created", task.getKey());
        return task;
    }

    @Override
    public Set<Project> getProjectsToCreate() {
        return getProjectUseCase.getAllProjects();
    }

    private Task getTaskInternal(TaskKey taskKey) {
        return taskRepository.findByKey(taskKey.toString())
                .orElseThrow(() -> NotFoundException.notFound("Task", taskKey.toString(), currentUserApi.currentEmail()));
    }
}
