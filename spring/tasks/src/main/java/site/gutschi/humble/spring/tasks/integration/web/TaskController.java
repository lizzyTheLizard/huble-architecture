package site.gutschi.humble.spring.tasks.integration.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.gutschi.humble.spring.common.error.NotAllowedException;
import site.gutschi.humble.spring.tasks.domain.api.*;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class TaskController {
    private final GetTasksUseCase getTasksUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final CommentTaskUseCase commentTaskUseCase;
    private final EditTaskUseCase editTaskUseCase;

    @GetMapping("/tasks/{key}")
    public String showTask(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        model.addAttribute("task", response.task());
        model.addAttribute("editable", response.editable());
        model.addAttribute("deletable", response.deletable());
        model.addAttribute("fields", response.project().getFields());
        model.addAttribute("users", response.projectUsers());
        return "task";
    }

    @GetMapping("/tasks/{key}/delete")
    public String deleteTaskView(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        if (!response.deletable()) {
            throw new NotAllowedException("You are not allowed to delete tasks in project '" + response.project().getKey() + "'");
        }
        model.addAttribute("task", response.task());
        return "delete";
    }

    @PostMapping("/tasks/{key}/delete")
    public String deleteTask(@PathVariable("key") String key) {
        final var deleteTaskRequest = new DeleteTaskRequest(key);
        deleteTaskUseCase.delete(deleteTaskRequest);
        return "redirect:/tasks/";
    }

    @PostMapping("/tasks/{key}/comment")
    public String addComment(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var commentTaskRequest = new CommentTaskRequest(key, body.get("comment"));
        commentTaskUseCase.comment(commentTaskRequest);
        return "redirect:/tasks/" + key;
    }

    @GetMapping("/tasks/{key}/edit")
    public String editTaskView(@PathVariable("key") String key, Model model) {
        final var response = getTasksUseCase.getTaskByKey(key);
        if (!response.editable()) {
            throw new NotAllowedException("You are not allowed to edit tasks in project '" + response.project().getKey() + "'");
        }
        model.addAttribute("states", TaskStatus.values());
        model.addAttribute("estimations", response.project().getEstimations());
        model.addAttribute("fields", response.project().getFields());
        model.addAttribute("users", response.projectUsers());
        model.addAttribute("task", response.task());
        return "edit";
    }

    @PostMapping("/tasks/{key}/edit")
    public String editTask(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var editTaskRequestBuilder = EditTaskRequest.builder()
                .taskKey(key)
                .title(body.get("title"))
                .description(body.get("description"))
                .estimation(Integer.parseInt(body.get("estimation")))
                .status(TaskStatus.valueOf(body.get("status")))
                .assignee(body.get("assigneeEmail"));
        body.keySet().stream()
                .filter(k -> k.startsWith("field_"))
                .forEach(k -> {
                    final var fieldKey = k.substring(6);
                    final var value = body.get(k);
                    editTaskRequestBuilder.additionalField(fieldKey, value);
                });
        final var editTaskRequest = editTaskRequestBuilder.build();
        editTaskUseCase.edit(editTaskRequest);
        return "redirect:/tasks/" + editTaskRequest.taskKey();
    }

    //TODO: Implement task overview
    /*
    @GetMapping("/tasks")
    public String showTasksOverview() {
        return "tasks";
    }

    @PostMapping("/tasks")
    public String createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        final var task = createTaskUseCase.create(createTaskRequest);
        return "redirect:/tasks/" + task.getKey();
    }
     */
}