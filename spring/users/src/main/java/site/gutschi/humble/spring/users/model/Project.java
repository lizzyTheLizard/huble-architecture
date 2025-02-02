package site.gutschi.humble.spring.users.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import site.gutschi.humble.spring.common.api.UserApi;
import site.gutschi.humble.spring.common.api.TimeApi;

import java.util.*;

@Getter
public class Project {
    private final UserApi userApi;
    private final TimeApi timeApi;
    private final String key;
    private String name;
    private boolean active;
    private final Collection<Integer> estimations = List.of(1,3,5);
    private final Collection<String> fields = List.of();
    private final Collection<ProjectRole> projectRoles = new LinkedList<>();
    private final Collection<ProjectHistoryEntry> historyEntries = new LinkedList<>();

    @Builder
    public Project(UserApi userApi, TimeApi timeApi, String key, String name, boolean active, @Singular Collection<ProjectRole> projectRoles, @Singular Collection<ProjectHistoryEntry> historyEntries) {
        this.userApi = userApi;
        this.timeApi = timeApi;
        this.key = key;
        this.name = name;
        this.active = active;
        this.projectRoles.addAll(projectRoles);
        this.historyEntries.addAll(historyEntries);
    }

    public void setName(String name){
        if(name.equals(this.name)) return;
        final var historyEntry = ProjectHistoryEntry.builder()
                .user(userApi.currentEmail())
                .timestamp(timeApi.now())
                .type(ProjectHistoryType.NAME_CHANGED)
                .oldValue(this.name)
                .newValue(name)
                .build();
        this.name = name;
        historyEntries.add(historyEntry);
    }

    public void setActive(boolean active){
        if(active == this.active) return;
        final var historyEntry = ProjectHistoryEntry.builder()
                .user(userApi.currentEmail())
                .timestamp(timeApi.now())
                .type(ProjectHistoryType.ACTIVATE_CHANGED)
                .oldValue(String.valueOf(this.active))
                .newValue(String.valueOf(active))
                .build();
        this.active = active;
        historyEntries.add(historyEntry);
    }


    public void setUserRole(User user, ProjectRoleType type){
        final var existingRole = projectRoles.stream()
                .filter(projectRole -> projectRole.user().equals(user))
                .findFirst();
        if(existingRole.isEmpty()) {
            final var historyEntry = ProjectHistoryEntry.builder()
                    .user(userApi.currentEmail())
                    .timestamp(timeApi.now())
                    .type(ProjectHistoryType.USER_ADDED)
                    .affectedUser(user.getEmail())
                    .newValue(type.name())
                    .build();
            projectRoles.add(new ProjectRole(user, type));
            historyEntries.add(historyEntry);
            return;
        }

        projectRoles.remove(existingRole.get());
        projectRoles.add(new ProjectRole(user, type));
        if(type == existingRole.get().type()) return;
        final var historyEntryBuilder = ProjectHistoryEntry.builder()
                .user(userApi.currentEmail())
                .timestamp(timeApi.now())
                .affectedUser(user.getEmail())
                .type(ProjectHistoryType.USER_ROLE_CHANGED)
                .oldValue(existingRole.get().type().name())
                .newValue(type.name());
        historyEntries.add(historyEntryBuilder.build());
    }

    public void removeUserRole(User user){
        final var existingRole = projectRoles.stream()
                .filter(projectRole -> projectRole.user().equals(user))
                .findFirst();
        if(existingRole.isEmpty()) {
            return;
        }
        projectRoles.remove(existingRole.get());
        final var historyEntryBuilder = ProjectHistoryEntry.builder()
                .user(userApi.currentEmail())
                .timestamp(timeApi.now())
                .affectedUser(user.getEmail())
                .type(ProjectHistoryType.USER_REMOVED)
                .oldValue(existingRole.get().type().name());
        historyEntries.add(historyEntryBuilder.build());
    }

    public Collection<ProjectRole> getProjectRoles() {
        return Collections.unmodifiableCollection(projectRoles);
    }

    public Optional<ProjectRoleType> getRole(String userEmail) {
        return projectRoles.stream()
                .filter(projectRole -> projectRole.user().getEmail().equals(userEmail))
                .map(ProjectRole::type)
                .findFirst();
    }

    public Collection<ProjectHistoryEntry> getHistoryEntries() {
        return Collections.unmodifiableCollection(historyEntries);
    }
}
