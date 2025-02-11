package site.gutschi.humble.spring.integration.ui.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.gutschi.humble.spring.tasks.api.*;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class TaskController {
    private final GetTasksUseCase getTasksUseCase;
    private final EditTaskUseCase editTaskUseCase;
    private final CreateTaskUseCase createTaskUseCase;

    @GetMapping("/tasks/{key}")
    public String showTask(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        model.addAttribute("task", response.task());
        model.addAttribute("editable", response.editable());
        model.addAttribute("deletable", response.deletable());
        model.addAttribute("users", response.project().getProjectUsers());
        return "task";
    }

    @GetMapping("/tasks/{key}/delete")
    public String deleteTaskView(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        if (!response.deletable()) {
            throw new EditTaskNotAllowedException(response.project().getKey());
        }
        model.addAttribute("task", response.task());
        return "deleteTask";
    }

    @PostMapping("/tasks/{key}/delete")
    public String deleteTask(@PathVariable("key") String key) {
        final var response = getTasksUseCase.getTaskByKey(key);
        final var deleteTaskRequest = new DeleteTaskRequest(key);
        editTaskUseCase.delete(deleteTaskRequest);
        return "redirect:/projects/" + response.project().getKey();
    }

    @PostMapping("/tasks/{key}/comment")
    public String addComment(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var commentTaskRequest = new CommentTaskRequest(key, body.get("comment"));
        editTaskUseCase.comment(commentTaskRequest);
        return "redirect:/tasks/" + key;
    }

    @GetMapping("/tasks/{key}/edit")
    public String editTaskView(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        if (!response.editable()) {
            throw new EditTaskNotAllowedException(response.project().getKey());
        }
        model.addAttribute("task", response.task());
        model.addAttribute("states", TaskStatus.values());
        model.addAttribute("estimations", response.project().getEstimations());
        model.addAttribute("users", response.project().getProjectUsers());
        return "editTask";
    }

    @PostMapping("/tasks/{key}/edit")
    public String editTask(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var assigneeEmail = body.get("assigneeEmail").isEmpty() ? null : body.get("assigneeEmail");
        final var estimation = body.get("estimation").isEmpty() ? null : Integer.parseInt(body.get("estimation"));
        final var request = EditTaskRequest.builder()
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
        final var request = FindTasksRequest.builder()
                .query(query)
                .page(page != null ? page : 1)
                .pageSize(10)
                .build();
        final var response = getTasksUseCase.findTasks(request);
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
        final var request = new CreateTaskRequest(body.get("project"), body.get("title"), body.get("description"));
        final var createdTask = createTaskUseCase.create(request);
        return "redirect:/tasks/" + createdTask.getKey();
    }

    //TODO: Direct actions to move task forwards / backwards
}