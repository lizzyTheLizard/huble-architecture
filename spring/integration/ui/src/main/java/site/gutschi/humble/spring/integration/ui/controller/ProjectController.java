package site.gutschi.humble.spring.integration.ui.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.api.CreateProjectUseCase;
import site.gutschi.humble.spring.users.api.EditProjectUseCase;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.model.ProjectRoleType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class ProjectController {
    private final GetProjectUseCase getProjectUseCase;
    private final EditProjectUseCase editProjectUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final CurrentUserApi currentUserApi;

    @GetMapping({"/index.html", "/", "/projects"})
    public String showProjectOverview(Model model) {
        final var response = getProjectUseCase.getAllProjects();
        model.addAttribute("projects", response);
        return "projects";
    }

    @GetMapping("/projects/{key}")
    public String showProject(@PathVariable("key") String key, Model model) {
        final var response = getProjectUseCase.getProject(key);
        model.addAttribute("project", response.project());
        model.addAttribute("users", response.project().getProjectUsers());
        model.addAttribute("manageable", response.manageable());
        model.addAttribute("currentProject", response.project());
        return "project";
    }

    @GetMapping("/projects/{key}/edit")
    public String editProjectView(@PathVariable("key") String key, Model model) {
        final var response = getProjectUseCase.getProject(key);
        if (!response.manageable()) {
            throw NotAllowedException.notAllowed("Project", response.project().getKey(), "edit", currentUserApi.currentEmail());
        }
        model.addAttribute("project", response.project());
        model.addAttribute("users", response.project().getProjectUsers());
        model.addAttribute("currentProject", response.project());
        return "editProject";
    }

    @PostMapping("/projects/{key}/edit")
    public String editProject(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var active = Boolean.parseBoolean(body.get("active"));
        final var estimations = body.get("estimations").isEmpty()
                ? Set.<Integer>of()
                : Arrays.stream(body.get("estimations").split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        final var request = new EditProjectUseCase.EditProjectRequest(key, body.get("name"), estimations, active);
        editProjectUseCase.editProject(request);
        return "redirect:/projects/" + key;
    }


    @GetMapping("/projects/{key}/assignUser")
    public String assignUserView(@PathVariable("key") String key, Model model) {
        final var response = getProjectUseCase.getProject(key);
        if (!response.manageable()) {
            throw NotAllowedException.notAllowed("Project", response.project().getKey(), "edit", currentUserApi.currentEmail());
        }
        model.addAttribute("project", response.project());
        model.addAttribute("users", response.project().getProjectUsers());
        model.addAttribute("roles", ProjectRoleType.values());
        model.addAttribute("currentProject", response.project());
        return "assignUser";
    }

    @PostMapping("/projects/{key}/assignUser")
    public String assignUser(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var user = body.get("user");
        final var role = ProjectRoleType.valueOf(body.get("role"));
        final var request = new EditProjectUseCase.AssignUserRequest(user, key, role);
        editProjectUseCase.assignUser(request);
        return "redirect:/projects/" + key;
    }

    @PostMapping("/projects/{key}/unassign")
    public String unassignUser(@PathVariable("key") String key, @RequestParam Map<String, String> body) {
        final var user = body.get("user");
        final var request = new EditProjectUseCase.UnAssignUserRequest(user, key);
        editProjectUseCase.unAssignUser(request);
        return "redirect:/projects/" + key;
    }

    @GetMapping("/projects/create")
    public String createProjectView(Model model) {
        if (!createProjectUseCase.canCreateProject()) {
            throw NotAllowedException.notAllowed("Project", "create", currentUserApi.currentEmail());
        }
        return "createProject";
    }

    @PostMapping("/projects/create")
    public String createProject(@RequestParam Map<String, String> body) {
        final var request = new CreateProjectUseCase.CreateProjectRequest(body.get("key"), body.get("name"));
        final var project = createProjectUseCase.createProject(request);
        return "redirect:/projects/" + project.getKey();
    }
}