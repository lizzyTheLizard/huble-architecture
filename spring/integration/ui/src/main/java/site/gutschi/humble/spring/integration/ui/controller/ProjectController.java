package site.gutschi.humble.spring.integration.ui.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class ProjectController {
    private final GetProjectUseCase getProjectUseCase;

    @GetMapping({"/index.html", "/", "/projects"})
    public String showProjectOverview(Model model) {
        final var response = getProjectUseCase.getAllProjects();
        model.addAttribute("projects", response);
        return "projects";
    }

    @GetMapping("/projects/{key}")
    public String showProject(@PathVariable("key") String key, Model model) {
        //TODO: Create View project
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "Project info site not yet implemented");
        return "error";
    }

    //TODO: Edit Project

    //TODO: Create Project
}