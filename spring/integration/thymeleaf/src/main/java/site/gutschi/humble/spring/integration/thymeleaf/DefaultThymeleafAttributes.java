package site.gutschi.humble.spring.integration.thymeleaf;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import site.gutschi.humble.spring.tasks.usecases.CreateTaskUseCase;
import site.gutschi.humble.spring.users.usecases.CreateProjectUseCase;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultThymeleafAttributes {
    private final CreateTaskUseCase createTaskUseCase;
    private final CreateProjectUseCase createProjectUseCase;

    //TODO UI: Internationalization
    @SuppressWarnings("SameReturnValue")
    @ModelAttribute("language")
    public String language() {
        return "en";
    }

    @ModelAttribute("canCreateTasks")
    public boolean canCreateTasks() {
        final var possibleProjects = createTaskUseCase.getProjectsToCreate();
        return !possibleProjects.isEmpty();
    }

    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("canCreateProjects")
    public boolean canCreateProjects() {
        return createProjectUseCase.canCreateProject();
    }
}
