package site.gutschi.humble.spring.tasks.domain;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.tasks.domain.ports.SearchCaller;
import site.gutschi.humble.spring.tasks.domain.ports.SearchCallerRequest;
import site.gutschi.humble.spring.tasks.domain.ports.SearchCallerResponse;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.domain.api.GetUserApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    public static final User CURRENT_USER = new User("Developer", "dev@example.com", "pwd", false);
    public static final Instant NOW = Instant.now();

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
            public void informUpdatedTasks(Task ...tasks) { }

            @Override
            public void clear() { }

            @Override
            public SearchCallerResponse findTasks(SearchCallerRequest request) {
                return new SearchCallerResponse(List.of(), 0);
            }
        };
    }


    @Bean
    GetProjectApi getProjectApi() {
        return new GetProjectApi() {
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
    GetUserApi getUserApi() {
        return userEmail -> userEmail.equals(CURRENT_USER.getEmail()) ? Optional.of(CURRENT_USER) : Optional.empty();
    }

    @Bean
    UserApi currentUserApi() {
        return new UserApi() {
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

    @Bean
    TimeApi timeApi() {
        return () -> NOW;
    }
}
