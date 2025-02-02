package site.gutschi.humble.spring.tasks.domain;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.tasks.domain.api.GetTasksRequest;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationCaller;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationsResponse;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.users.domain.api.GetProjectApi;
import site.gutschi.humble.spring.users.domain.api.GetUserApi;
import site.gutschi.humble.spring.users.model.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    public static final User CURRENT_USER = new User("Developer", "dev@example.com", "pwd", false);
    public static final Instant NOW = Instant.now();

    @Bean
    CheckImplementationCaller checkImplementationCaller() {
        return taskId -> new CheckImplementationsResponse(List.of());
    }

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
            public Collection<Task> findTasksWithoutPaging(GetTasksRequest request) {
                return List.of();
            }

            @Override
            public Collection<Task> findTasks(GetTasksRequest request) {
                return List.of();
            }

            @Override
            public int nextId(String projectKey) {
                return 42;
            }
        };
    }

    @Bean
    GetProjectApi getProjectApi() {
        return projectKey -> Optional.empty();
    }

    @Bean
    GetUserApi getUserApi() {
        return new GetUserApi() {
            @Override
            public Optional<User> getUser(String userEmail) {
                return userEmail.equals(CURRENT_USER.getEmail()) ? Optional.of(CURRENT_USER) : Optional.empty();
            }

            @Override
            public Collection<User> getUserForProject(String projectKey) {
                return List.of();
            }
        };
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
