package site.gutschi.humble.spring.integration.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryProjectRepository implements ProjectRepository {
    private final List<Project> projects = new LinkedList<>();

    @Override
    public void save(Project project) {
        projects.removeIf(p -> p.getKey().equals(project.getKey()));
        projects.add(project);
    }

    @Override
    public Optional<Project> findByKey(String key) {
        return projects.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst();
    }

    @Override
    public Collection<Project> findAll() {
        return Collections.unmodifiableList(projects);
    }

    @Override
    public Collection<Project> findAllForUser(User user) {
        return projects.stream()
                .filter(p -> p.getRole(user.getEmail()).isPresent())
                .toList();
    }
}
