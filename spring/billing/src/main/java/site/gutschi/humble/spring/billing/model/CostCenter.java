package site.gutschi.humble.spring.billing.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import site.gutschi.humble.spring.users.model.Project;

import java.util.*;

//TODO: Document
public class CostCenter {
    @Getter
    private final int id;
    private final Set<Project> projects;
    private final List<String> address;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private boolean active;

    @Builder
    public CostCenter(int id, String name, List<String> address, String email, boolean active, @Singular Set<Project> projects) {
        this.id = id;
        this.name = name;
        this.address = new ArrayList<>(address);
        this.email = email;
        this.active = active;
        this.projects = new HashSet<>(projects);
    }

    public static CostCenter create(int id, String name, List<String> address, String email) {
        return CostCenter.builder()
                .id(id)
                .name(name)
                .address(address)
                .email(email)
                .active(true)
                .build();
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet(projects);
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }

    public List<String> getAddress() {
        return Collections.unmodifiableList(address);
    }

    public void setAddress(List<String> address) {
        this.address.clear();
        this.address.addAll(address);
    }
}
