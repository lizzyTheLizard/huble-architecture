package site.gutschi.humble.spring.integration.ui.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    //TODO: Create, Edit, View projects
}