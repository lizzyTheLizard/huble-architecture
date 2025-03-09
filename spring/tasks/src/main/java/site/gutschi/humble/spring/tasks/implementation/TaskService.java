package site.gutschi.humble.spring.tasks.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.tasks.api.CreateTaskUseCase;
import site.gutschi.humble.spring.tasks.api.EditTaskUseCase;
import site.gutschi.humble.spring.tasks.api.GetTasksApi;
import site.gutschi.humble.spring.tasks.api.ViewTasksUseCase;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectRole;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService implements EditTaskUseCase, ViewTasksUseCase, GetTasksApi, CreateTaskUseCase {
    private final CurrentUserApi currentUserApi;
    private final GetProjectApi getProjectApi;
    private final TaskRepository taskRepository;
    private final CanAccessTasksPolicy canAccessTasksPolicy;
    private final ProjectActivePolicy projectActivePolicy;
    private final NotDeletedPolicy notDeletedPolicy;
    private final SearchCaller searchCaller;

    @Override
    public void edit(EditTaskRequest request) {
        final var existingTask = getTaskInternal(request.taskKey());
        final var project = existingTask.getProject();
        final var currentUser = currentUserApi.getCurrentUser();
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessTasksPolicy.ensureCanEditTasksInProject(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setEstimation(request.estimation(), currentUser);
        if (request.assignee() != null) {
            final var assignee = project.getProjectRoles().stream()
                    .map(ProjectRole::user)
                    .filter(u -> u.getEmail().equals(request.assignee()))
                    .findFirst()
                    .orElseThrow(() -> NotFoundException.notFound("User", request.assignee(), currentUserApi.getCurrentUser().getEmail()));
            existingTask.setAssignee(assignee, currentUser);
        } else {
            existingTask.setAssignee(null, currentUser);
        }
        existingTask.setStatus(request.status(), currentUser);
        existingTask.setTitle(request.title(), currentUser);
        existingTask.setDescription(request.description(), currentUser);
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} edited", existingTask.getKey());
    }

    @Override
    public void comment(CommentTaskRequest request) {
        final var existingTask = getTaskInternal(request.taskKey());
        final var currentUser = currentUserApi.getCurrentUser();
        final var project = existingTask.getProject();
        projectActivePolicy.ensureProjectIsActive(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.addComment(request.comment(), currentUser);
        taskRepository.save(existingTask);
        searchCaller.informUpdatedTasks(existingTask);
        log.info("Task {} commented", existingTask.getKey());
    }

    @Override
    public void delete(TaskKey taskKey) {
        final var existingTask = getTaskInternal(taskKey);
        final var currentUser = currentUserApi.getCurrentUser();
        final var project = existingTask.getProject();
        projectActivePolicy.ensureProjectIsActive(project);
        canAccessTasksPolicy.ensureCanDeleteTasksInProject(project);
        notDeletedPolicy.ensureNotDeleted(existingTask);
        existingTask.setDeleted(currentUser);
        taskRepository.save(existingTask);
        searchCaller.informDeletedTasks(existingTask);
        log.info("Task {} deleted", existingTask.getKey());
    }

    @Override
    public GetTaskResponse getTaskByKey(TaskKey taskKey) {
        final var existingTask = getTaskInternal(taskKey);
        final var project = existingTask.getProject();
        notDeletedPolicy.ensureNotDeleted(existingTask);
        final var editable = canAccessTasksPolicy.canEditTasksInProject(project);
        final var deletable = canAccessTasksPolicy.canDeleteTasksInProject(project);
        return new GetTaskResponse(existingTask, editable, deletable, project);
    }

    @Override
    public FindTasksResponse findTasks(FindTasksRequest request) {
        final var projects = getProjectApi.getAllProjects();
        final var searchCallerRequest = new SearchCaller.SearchCallerRequest(request.query(), request.page(), request.pageSize(), projects);
        final var searchCallerResponse = searchCaller.findTasks(searchCallerRequest);
        return new FindTasksResponse(searchCallerResponse.tasks(), projects, searchCallerResponse.total());
    }

    @Override
    public Collection<Task> getTasksForProject(Project project) {
        return taskRepository.findByProject(project);
    }

    @Override
    public Task create(CreateTaskRequest request) {
        final var project = getProjectApi.getProject(request.projectKey());
        canAccessTasksPolicy.ensureCanEditTasksInProject(project);
        projectActivePolicy.ensureProjectIsActive(project);
        final var nextId = taskRepository.nextId(project);
        final var currentUser = currentUserApi.getCurrentUser();
        final var task = Task.createNew(project, nextId, request.title(), request.description(), currentUser);
        taskRepository.save(task);
        searchCaller.informUpdatedTasks(task);
        log.info("Task {} created", task.getKey());
        return task;
    }

    @Override
    public Collection<Project> getProjectsToCreate() {
        return getProjectApi.getAllProjects();
    }

    private Task getTaskInternal(TaskKey taskKey) {
        return taskRepository.findByKey(taskKey.toString())
                .orElseThrow(() -> NotFoundException.notFound("Task", taskKey.toString(), currentUserApi.getCurrentUser().getEmail()));
    }
}
