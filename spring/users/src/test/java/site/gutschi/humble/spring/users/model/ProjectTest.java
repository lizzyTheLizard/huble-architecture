package site.gutschi.humble.spring.users.model;

import org.junit.jupiter.api.Test;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.api.TimeApi;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    private static final String USER = "TestUser";
    private static final CurrentUserApi USER_API = new CurrentUserApi() {
        @Override
        public String currentEmail() {
            return USER;
        }

        @Override
        public boolean isSystemAdmin() {
            return false;
        }
    };

    private static final Instant NOW = Instant.now();
    private static final TimeApi TIME_API = () -> NOW;

    @Test
    void setName() {
        final var newName = "New Name";
        final var project = createProject();
        final var oldName = project.getName();

        project.setName(newName);

        assertThat(project.getName())
                .isEqualTo(newName);
        assertThat(project.getHistoryEntries())
                .contains(new ProjectHistoryEntry(USER, NOW, ProjectHistoryType.NAME_CHANGED, null, oldName, newName));
    }

    @Test
    void setName_ignoreUnchanged() {
        final var project = createProject();
        final var oldName = project.getName();
        final var historySize = project.getHistoryEntries().size();

        project.setName(oldName);

        assertThat(project.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setActive() {
        final var project = createProject();

        project.setActive(false);

        assertThat(project.isActive())
                .isEqualTo(false);
        assertThat(project.getHistoryEntries())
                .contains(new ProjectHistoryEntry(USER, NOW, ProjectHistoryType.ACTIVATE_CHANGED, null, "true", "false"));
    }

    @Test
    void setActive_ignoreUnchanged() {
        final var project = createProject();
        final var historySize = project.getHistoryEntries().size();

        project.setActive(true);

        assertThat(project.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setUserRole_newUser() {
        final var project = createProject();
        final var user = new User("user", "user@example.com", "pwd", false);

        project.setUserRole(user, ProjectRoleType.ADMIN);

        assertThat(project.getProjectRoles())
                .contains(new ProjectRole(user, ProjectRoleType.ADMIN));
        assertThat(project.getRole(user.getEmail()))
                .contains(ProjectRoleType.ADMIN);
        assertThat(project.getHistoryEntries())
                .contains(new ProjectHistoryEntry(USER, NOW, ProjectHistoryType.USER_ADDED, user.getEmail(), null, ProjectRoleType.ADMIN.name()));
    }

    @Test
    void setUserRole_changedUser() {
        final var project = createProject();
        final var user = new User("user", "user@example.com", "pwd", false);
        project.setUserRole(user, ProjectRoleType.ADMIN);

        project.setUserRole(user, ProjectRoleType.STAKEHOLDER);

        assertThat(project.getProjectRoles())
                .contains(new ProjectRole(user, ProjectRoleType.STAKEHOLDER));
        assertThat(project.getRole(user.getEmail()))
                .contains(ProjectRoleType.STAKEHOLDER);
        assertThat(project.getHistoryEntries())
                .contains(new ProjectHistoryEntry(USER, NOW, ProjectHistoryType.USER_ROLE_CHANGED, user.getEmail(), ProjectRoleType.ADMIN.name(), ProjectRoleType.STAKEHOLDER.name()));
    }

    @Test
    void setUserRole_unchangedUser() {
        final var project = createProject();
        final var user = new User("user", "user@example.com", "pwd", false);
        project.setUserRole(user, ProjectRoleType.ADMIN);
        final var historySize = project.getHistoryEntries().size();

        project.setUserRole(user, ProjectRoleType.ADMIN);

        assertThat(project.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void removeUserRole() {
        final var project = createProject();
        final var user = new User("user", "user@example.com", "pwd", false);
        project.setUserRole(user, ProjectRoleType.ADMIN);

        project.removeUserRole(user);

        assertThat(project.getProjectRoles())
                .doesNotContain(new ProjectRole(user, ProjectRoleType.ADMIN));
        assertThat(project.getRole(user.getEmail()))
                .isEmpty();
        assertThat(project.getHistoryEntries())
                .contains(new ProjectHistoryEntry(USER, NOW, ProjectHistoryType.USER_REMOVED, user.getEmail(), ProjectRoleType.ADMIN.name(), null));
    }

    @Test
    void removeUserRole_unchangedUser() {
        final var project = createProject();
        final var user = new User("user", "user@example.com", "pwd", false);
        project.removeUserRole(user);
        final var historySize = project.getHistoryEntries().size();

        project.removeUserRole(user);

        assertThat(project.getHistoryEntries())
                .hasSize(historySize);
    }

    private Project createProject() {
        return Project.builder()
                .currentUserApi(USER_API)
                .timeApi(TIME_API)
                .key("key")
                .name("name")
                .active(true)
                .projectRole(new ProjectRole(new User("a", "B", "pwd", false), ProjectRoleType.ADMIN))
                .build();
    }

}