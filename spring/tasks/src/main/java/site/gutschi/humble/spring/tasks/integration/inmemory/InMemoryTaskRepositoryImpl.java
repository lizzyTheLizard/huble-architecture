package site.gutschi.humble.spring.tasks.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.*;

@Service
public class InMemoryTaskRepositoryImpl implements TaskRepository {
    private final Map<String, Integer> nextId = new HashMap<>();
    private final Collection<Task> tasks = new LinkedList<>();

    public InMemoryTaskRepositoryImpl(UserApi userApi, TimeApi timeApi) {
        nextId.put("PRO", 14);
        for (var i = 1; i < 20; i++) {
            tasks.add(Task.builder()
                    .id(i)
                    .creatorEmail("test@example.com")
                    .comment(new Comment("test@example.com", timeApi.now(), "This is a comment"))
                    .description("This is the description")
                    .projectKey("PRO")
                    .status(TaskStatus.FUNNEL)
                    .estimation(3)
                    .assigneeEmail("test@example.com")
                    .timeApi(timeApi)
                    .userApi(userApi)
                    .title("Title of PRO-" + i)
                    .build());
        }
    }

    @Override
    public Optional<Task> findByKey(String taskKey) {
        return tasks.stream()
                .filter(task -> task.getKey().equals(taskKey))
                .findFirst();
    }

    @Override
    public void save(Task existingTask) {
        tasks.removeIf(task -> task.getKey().equals(existingTask.getKey()));
        tasks.add(existingTask);
    }

    @Override
    public int nextId(String projectKey) {
        final var next = nextId.get(projectKey);
        nextId.put(projectKey, next + 1);
        return next;
    }
}
