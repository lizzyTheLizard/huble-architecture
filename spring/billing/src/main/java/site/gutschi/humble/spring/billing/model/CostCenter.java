package site.gutschi.humble.spring.billing.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A cost center is the billing address for a set of projects.
 */
@Getter
@Setter
public class CostCenter {
    /**
     * The unique identifier of the cost center. Is null before the cost center is persisted.
     */
    private final Integer id;
    /**
     * The billing address of the cost center including the name of the cost center.
     */
    private List<String> address;
    /**
     * Projects that are assigned to this cost center.
     */
    private Set<Project> projects;
    /**
     * The name of the cost center.
     */
    private String name;
    /**
     * The email address of the cost center.
     */
    private String email;
    /**
     * Is this cost center deleted?
     */
    private boolean deleted;

    @Builder
    public CostCenter(Integer id, String name, List<String> address, String email, boolean deleted, @Singular Set<Project> projects) {
        this.id = id;
        this.name = name;
        this.address = Collections.unmodifiableList(address);
        this.email = email;
        this.deleted = deleted;
        this.projects = Collections.unmodifiableSet(projects);
    }

    public static CostCenter create(String name, List<String> address, String email) {
        return CostCenter.builder()
                .id(null)
                .name(name)
                .address(address)
                .email(email)
                .deleted(false)
                .build();
    }

    /**
     * Add a project to the cost center.
     */
    public void addProject(Project project) {
        projects = Stream.concat(projects.stream(), Stream.of(project))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Remove a project from the cost center. Ignored if the project is not assigned to the cost center.
     */
    public void removeProject(Project project) {
        projects = projects.stream()
                .filter(p -> !p.equals(project))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * The billing address of the cost center including the name of the cost center.
     */
    public void setAddress(List<String> address) {
        this.address = Collections.unmodifiableList(address);
    }
}
