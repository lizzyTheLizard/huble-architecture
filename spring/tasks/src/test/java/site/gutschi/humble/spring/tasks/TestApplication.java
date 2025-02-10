package site.gutschi.humble.spring.tasks;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.ports.SearchCallerResponse;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.api.GetProjectUseCase;
import site.gutschi.humble.spring.users.api.GetUserUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    public static final User CURRENT_USER = new User("Developer", "dev@example.com", "pwd", false);

    @Bean
    TaskRepository taskRepository() {
        return new TaskRepository() {

            @Override
            public Optional<Task> findByKey(String s) {
                return Optional.empty();
            }

            @Override
            public void save(Task existingTask) {

            }

            @Override
            public int nextId(String projectKey) {
                return 42;
            }
        };
    }

    @Bean
    SearchCaller searchCaller() {
        return new SearchCaller() {
            @Override
            public void informUpdatedTasks(Task... tasks) {
            }

            @Override
            public void clear() {
            }

            @Override
            public SearchCallerResponse findTasks(SearchCallerRequest request) {
                return new SearchCallerResponse(List.of(), 0);
            }
        };
    }

    @Bean
    GetProjectUseCase getProjectApi() {
        return new GetProjectUseCase() {
            @Override
            public Optional<Project> getProject(String projectKey) {
                return Optional.empty();
            }

            @Override
            public Collection<Project> getAllProjects() {
                return List.of();
            }
        };
    }

    @Bean
    GetUserUseCase getUserApi() {
        return userEmail -> userEmail.equals(CURRENT_USER.getEmail()) ? Optional.of(CURRENT_USER) : Optional.empty();
    }

    @Bean
    CurrentUserApi currentUserApi() {
        return new CurrentUserApi() {
            @Override
            public String currentEmail() {
                return CURRENT_USER.getEmail();
            }

            @Override
            public boolean isSystemAdmin() {
                return false;
            }
        };
    }
}
