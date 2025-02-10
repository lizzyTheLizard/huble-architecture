package site.gutschi.humble.spring.tasks;


import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.SearchCallerResponse;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.api.GetUserUseCase;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;
import java.util.Optional;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    public static final User CURRENT_USER = new User("dev@example.com", "pwd", false, "Developer");

    @Bean
    TaskRepository taskRepository() {
        final var taskRepository = Mockito.mock(TaskRepository.class);
        Mockito.when(taskRepository.findByKey(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(taskRepository.nextId(Mockito.anyString())).thenReturn(42);
        return taskRepository;
    }

    @Bean
    SearchCaller searchCaller() {
        final var searchCaller = Mockito.mock(SearchCaller.class);
        Mockito.when(searchCaller.findTasks(Mockito.any())).thenReturn(new SearchCallerResponse(List.of(), 0));
        return searchCaller;
    }

    @Bean
    GetProjectUseCase getProjectUseCase() {
        final var getProjectUseCase = Mockito.mock(GetProjectUseCase.class);
        Mockito.when(getProjectUseCase.getProject(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(getProjectUseCase.getAllProjects()).thenReturn(List.of());
        return getProjectUseCase;
    }

    @Bean
    GetUserUseCase getUserUseCase() {
        final var getUserUseCase = Mockito.mock(GetUserUseCase.class);
        Mockito.when(getUserUseCase.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(getUserUseCase.getUser(CURRENT_USER.getEmail())).thenReturn(Optional.of(CURRENT_USER));
        return getUserUseCase;
    }

    @Bean
    CurrentUserApi currentUserApi() {
        final var currentUserApi = Mockito.mock(CurrentUserApi.class);
        Mockito.when(currentUserApi.currentEmail()).thenReturn(CURRENT_USER.getEmail());
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        return currentUserApi;
    }
}
