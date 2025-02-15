package site.gutschi.humble.spring.integration.ui;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.api.CreateTaskUseCase;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultThymeleafAttributes {
    private final CurrentUserApi currentUserApi;
    private final CreateTaskUseCase createTaskUseCase;

    //TODO: Internationalization
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
        return currentUserApi.isSystemAdmin();
    }
}
