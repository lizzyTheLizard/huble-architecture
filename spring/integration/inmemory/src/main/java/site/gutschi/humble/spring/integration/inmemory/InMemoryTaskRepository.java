package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;

import java.util.*;

@Service
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Integer> nextId = new HashMap<>();
    private final Collection<Task> tasks = new LinkedList<>();

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
        final var next = nextId.getOrDefault(projectKey, 1);
        nextId.put(projectKey, next + 1);
        return next;
    }
}
