package site.gutschi.humble.spring.users.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.util.*;

/**
 * A project in the system. Project are identified by their key and are always created by a system admin.
 * A project can be deactivated, which means it is not editable anymore. A project can have multiple users with different roles.
 * A project will keep a journal of each change.
 * At least one user must be an admin.
 */
public class Project {
    @Getter
    private final String key;
    @Singular
    private final Set<ProjectRole> projectRoles;
    @Singular
    private final Set<ProjectHistoryEntry> historyEntries;
    @Singular
    private final Set<Integer> estimations;
    @Getter
    private String name;
    @Getter
    private boolean active;

    @Builder
    private Project(String key, String name, boolean active, @Singular Collection<Integer> estimations, @Singular Collection<ProjectRole> projectRoles, @Singular Collection<ProjectHistoryEntry> historyEntries) {
        this.key = key;
        this.name = name;
        this.active = active;
        this.projectRoles = new HashSet<>(projectRoles);
        this.historyEntries = new HashSet<>(historyEntries);
        this.estimations = new HashSet<>(estimations);
    }

    public static Project createNew(String key, String name, User owner) {
        final var initialAdminRole = new ProjectRole(owner, ProjectRoleType.ADMIN);
        final var historyEntry = ProjectHistoryEntry.builder()
                .user(owner)
                .timestamp(TimeHelper.now())
                .type(ProjectHistoryType.CREATED)
                .build();
        return Project.builder()
                .key(key)
                .name(name)
                .projectRole(initialAdminRole)
                .estimation(1)
                .estimation(3)
                .estimation(5)
                .active(true)
                .historyEntry(historyEntry)
                .build();
    }

    public void setName(String name, User currentUser) {
        if (name.equals(this.name)) return;
        final var historyEntry = ProjectHistoryEntry.builder()
                .user(currentUser)
                .timestamp(TimeHelper.now())
                .type(ProjectHistoryType.NAME_CHANGED)
                .oldValue(this.name)
                .newValue(name)
                .build();
        this.name = name;
        historyEntries.add(historyEntry);
    }

    public void setActive(boolean active, User currentUser) {
        if (active == this.active) return;
        final var historyEntry = ProjectHistoryEntry.builder()
                .user(currentUser)
                .timestamp(TimeHelper.now())
                .type(ProjectHistoryType.ACTIVATE_CHANGED)
                .oldValue(String.valueOf(this.active))
                .newValue(String.valueOf(active))
                .build();
        this.active = active;
        historyEntries.add(historyEntry);
    }

    public void setUserRole(User user, ProjectRoleType type, User currentUser) {
        final var existingRole = projectRoles.stream()
                .filter(projectRole -> projectRole.user().equals(user))
                .findFirst();
        if (existingRole.isEmpty()) {
            final var historyEntry = ProjectHistoryEntry.builder()
                    .user(currentUser)
                    .timestamp(TimeHelper.now())
                    .type(ProjectHistoryType.USER_ADDED)
                    .affectedUser(user)
                    .newValue(type.name())
                    .build();
            projectRoles.add(new ProjectRole(user, type));
            historyEntries.add(historyEntry);
            return;
        }

        projectRoles.remove(existingRole.get());
        projectRoles.add(new ProjectRole(user, type));
        if (type == existingRole.get().type()) return;
        final var historyEntryBuilder = ProjectHistoryEntry.builder()
                .user(currentUser)
                .timestamp(TimeHelper.now())
                .affectedUser(user)
                .type(ProjectHistoryType.USER_ROLE_CHANGED)
                .oldValue(existingRole.get().type().name())
                .newValue(type.name());
        historyEntries.add(historyEntryBuilder.build());
    }

    public void removeUserRole(User user, User currentUser) {
        final var existingRole = projectRoles.stream()
                .filter(projectRole -> projectRole.user().equals(user))
                .findFirst();
        if (existingRole.isEmpty()) {
            return;
        }
        projectRoles.remove(existingRole.get());
        final var historyEntryBuilder = ProjectHistoryEntry.builder()
                .user(currentUser)
                .timestamp(TimeHelper.now())
                .affectedUser(user)
                .type(ProjectHistoryType.USER_REMOVED)
                .oldValue(existingRole.get().type().name());
        historyEntries.add(historyEntryBuilder.build());
    }

    public Set<ProjectRole> getProjectRoles() {
        return Collections.unmodifiableSet(projectRoles);
    }

    public Optional<ProjectRoleType> getRole(User user) {
        return projectRoles.stream()
                .filter(projectRole -> projectRole.user().getEmail().equals(user.getEmail()))
                .map(ProjectRole::type)
                .findFirst();
    }

    public Map<String, User> getProjectUsers() {
        return projectRoles.stream()
                .collect(HashMap::new, (map, role) -> map.put(role.user().getEmail(), role.user()), HashMap::putAll);
    }

    public Set<ProjectHistoryEntry> getHistoryEntries() {
        return Collections.unmodifiableSet(historyEntries);
    }

    public Set<Integer> getEstimations() {
        return Collections.unmodifiableSet(estimations);
    }

    public void setEstimations(Set<Integer> estimations) {
        this.estimations.clear();
        this.estimations.addAll(estimations);
    }
}
