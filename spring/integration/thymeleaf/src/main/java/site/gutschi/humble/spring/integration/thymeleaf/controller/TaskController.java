package site.gutschi.humble.spring.integration.thymeleaf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.usecases.CreateTaskUseCase;
import site.gutschi.humble.spring.tasks.usecases.EditTaskUseCase;
import site.gutschi.humble.spring.tasks.usecases.ViewTasksUseCase;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class TaskController {
    private final ViewTasksUseCase viewTasksUseCase;
    private final EditTaskUseCase editTaskUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final CurrentUserApi currentUserApi;

    @GetMapping("/tasks/{key}")
    public String showTask(@PathVariable("key") String keyStr, Model model) {
        final var key = TaskKey.fromString(keyStr);
        final var response = viewTasksUseCase.getTaskByKey(key);
        model.addAttribute("task", response.task());
        model.addAttribute("editable", response.editable());
        model.addAttribute("deletable", response.deletable());
        model.addAttribute("users", response.project().getProjectUsers());
        model.addAttribute("currentProject", response.project());
        return "task";
    }

    @GetMapping("/tasks/{key}/delete")
    public String deleteTaskView(@PathVariable("key") String keyStr, Model model) {
        final var key = TaskKey.fromString(keyStr);
        final var response = viewTasksUseCase.getTaskByKey(key);
        if (!response.deletable()) {
            throw NotAllowedException.notAllowed("Project", response.project().getKey(), "delete task", currentUserApi.currentEmail());
        }
        model.addAttribute("task", response.task());
        model.addAttribute("currentProject", response.project());
        return "deleteTask";
    }

    @PostMapping("/tasks/{key}/delete")
    public String deleteTask(@PathVariable("key") String keyStr) {
        final var key = TaskKey.fromString(keyStr);
        final var response = viewTasksUseCase.getTaskByKey(key);
        editTaskUseCase.delete(key);
        return "redirect:/projects/" + response.project().getKey();
    }

    @PostMapping("/tasks/{key}/comment")
    public String addComment(@PathVariable("key") String keyStr, @RequestParam Map<String, String> body) {
        final var key = TaskKey.fromString(keyStr);
        final var commentTaskRequest = new EditTaskUseCase.CommentTaskRequest(key, body.get("comment"));
        editTaskUseCase.comment(commentTaskRequest);
        return "redirect:/tasks/" + key;
    }

    @GetMapping("/tasks/{key}/edit")
    public String editTaskView(@PathVariable("key") String keyStr, Model model) {
        final var key = TaskKey.fromString(keyStr);
        final var response = viewTasksUseCase.getTaskByKey(key);
        if (!response.editable()) {
            throw NotAllowedException.notAllowed("Project", response.project().getKey(), "edit task", currentUserApi.currentEmail());
        }
        model.addAttribute("task", response.task());
        model.addAttribute("states", TaskStatus.values());
        model.addAttribute("estimations", response.project().getEstimations());
        model.addAttribute("users", response.project().getProjectUsers());
        model.addAttribute("currentProject", response.project());
        return "editTask";
    }

    @PostMapping("/tasks/{key}/edit")
    public String editTask(@PathVariable("key") String keyStr, @RequestParam Map<String, String> body) {
        final var key = TaskKey.fromString(keyStr);
        final var assigneeEmail = body.get("assigneeEmail").isEmpty() ? null : body.get("assigneeEmail");
        final var estimation = body.get("estimation").isEmpty() ? null : Integer.parseInt(body.get("estimation"));
        final var request = EditTaskUseCase.EditTaskRequest.builder()
                .taskKey(key)
                .title(body.get("title"))
                .description(body.get("description"))
                .estimation(estimation)
                .status(TaskStatus.valueOf(body.get("status")))
                .assignee(assigneeEmail)
                .build();
        editTaskUseCase.edit(request);
        return "redirect:/tasks/" + request.taskKey();
    }

    @GetMapping("/tasks")
    public String showTasksOverview(@RequestParam(name = "page", required = false) Integer page,
                                    @RequestParam(name = "query") String query, Model model) {
        final var request = ViewTasksUseCase.FindTasksRequest.builder()
                .query(query)
                .page(page != null ? page : 1)
                .pageSize(10)
                .build();
        final var response = viewTasksUseCase.findTasks(request);
        final var projectUsers = response.projects().stream()
                .flatMap(project -> project.getProjectUsers().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        model.addAttribute("tasks", response.tasks());
        model.addAttribute("users", projectUsers);
        model.addAttribute("page", request.page());
        final var numberOfPage = Math.ceilDiv(response.total(), request.pageSize());
        model.addAttribute("pages", numberOfPage);
        return "tasks";
    }

    @GetMapping("/tasks/create")
    public String createTaskView(Model model) {
        final var projects = createTaskUseCase.getProjectsToCreate();
        model.addAttribute("projects", projects);
        return "createTask";
    }

    @PostMapping("/tasks/create")
    public String createTask(@RequestParam Map<String, String> body) {
        final var request = new CreateTaskUseCase.CreateTaskRequest(body.get("project"), body.get("title"), body.get("description"));
        final var createdTask = createTaskUseCase.create(request);
        return "redirect:/tasks/" + createdTask.getKey();
    }

    //TODO UI: Direct actions to move task forwards / backwards
}