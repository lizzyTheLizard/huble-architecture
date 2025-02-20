package site.gutschi.humble.spring.users.ports;

import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.Optional;
import java.util.Set;

public interface ProjectRepository {
    /**
     * Save a changed or new project
     *
     * @param project The project to save
     */
    void save(Project project);

    /**
     * Find a project by its key
     *
     * @param key The key of the project
     * @return The project or empty if not found
     */
    Optional<Project> findByKey(String key);

    /**
     * Find all projects
     *
     * @return All projects
     */
    Set<Project> findAll();

    /**
     * Find all projects that are connected to a user by a role.
     *
     * @param user The user
     * @return All projects for the user
     */
    Set<Project> findAllForUser(User user);
}
