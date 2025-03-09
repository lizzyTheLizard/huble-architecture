package site.gutschi.humble.spring.tasks;

import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;

@SpringBootApplication
public class TasksTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TasksTestApplication.class);
    }

    @Bean
    CurrentUserApi currentUserApi() {
        return Mockito.mock(CurrentUserApi.class);
    }

    @Bean
    SearchCaller searchCaller() {
        return Mockito.mock(SearchCaller.class);
    }

    @Bean
    TaskRepository taskRepository() {
        return Mockito.mock(TaskRepository.class);
    }

    @Bean
    GetProjectApi getProjectUseCase() {
        return Mockito.mock(GetProjectApi.class);
    }
}
