package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Integer> nextId = new HashMap<>();
    private final Set<Task> tasks = new HashSet<>();

    @Override
    public Optional<Task> findByKey(String taskKey) {
        return tasks.stream()
                .filter(task -> task.getKey().toString().equals(taskKey))
                .findFirst();
    }

    @Override
    public void save(Task existingTask) {
        tasks.removeIf(task -> task.getKey().equals(existingTask.getKey()));
        tasks.add(existingTask);
    }

    @Override
    public int nextId(Project project) {
        final var next = nextId.getOrDefault(project.getKey(), 1);
        nextId.put(project.getKey(), next + 1);
        return next;
    }

    @Override
    public Set<Task> findByProject(Project project) {
        return tasks.stream()
                .filter(task -> task.getProject().equals(project))
                .collect(Collectors.toSet());
    }
}
