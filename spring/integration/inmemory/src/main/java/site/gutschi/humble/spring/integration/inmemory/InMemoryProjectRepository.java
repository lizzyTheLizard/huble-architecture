package site.gutschi.humble.spring.integration.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryProjectRepository implements ProjectRepository {
    private final Set<Project> projects = new HashSet<>();

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
    public Set<Project> findAll() {
        return Collections.unmodifiableSet(projects);
    }

    @Override
    public Set<Project> findAllForUser(User user) {
        return projects.stream()
                .filter(p -> p.getRole(user).isPresent())
                .collect(Collectors.toUnmodifiableSet());
    }
}
