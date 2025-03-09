package site.gutschi.humble.spring.users.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    private Instant now;
    private User currentUser;
    private User newUser;
    private Project project;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        final var owner = User.builder().email("owner@example.com").name("Owner").build();
        newUser = User.builder().email("new@example.com").name("New").build();
        project = Project.createNew("KEY", "Name", owner);
        now = Instant.now();
        TimeHelper.setNow(now);
    }

    @Test
    void setName() {
        final var newName = "New Name";
        final var oldName = project.getName();

        project.setName(newName, currentUser);

        assertThat(project.getName()).isEqualTo(newName);
        assertThat(project.getHistoryEntries()).contains(new ProjectHistoryEntry(currentUser, now, ProjectHistoryType.NAME_CHANGED, null, oldName, newName));
    }

    @Test
    void setName_ignoreUnchanged() {
        final var oldName = project.getName();
        final var historySize = project.getHistoryEntries().size();

        project.setName(oldName, currentUser);

        assertThat(project.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setActive() {
        project.setActive(false, currentUser);

        assertThat(project.isActive()).isEqualTo(false);
        assertThat(project.getHistoryEntries()).contains(new ProjectHistoryEntry(currentUser, now, ProjectHistoryType.ACTIVATE_CHANGED, null, "true", "false"));
    }

    @Test
    void setActive_ignoreUnchanged() {
        final var historySize = project.getHistoryEntries().size();

        project.setActive(true, currentUser);

        assertThat(project.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setUserRole_newUser() {
        project.setUserRole(newUser, ProjectRoleType.ADMIN, currentUser);

        assertThat(project.getProjectRoles()).contains(new ProjectRole(newUser, ProjectRoleType.ADMIN));
        assertThat(project.getRole(newUser)).contains(ProjectRoleType.ADMIN);
        assertThat(project.getHistoryEntries()).contains(new ProjectHistoryEntry(currentUser, now, ProjectHistoryType.USER_ADDED, newUser, null, ProjectRoleType.ADMIN.name()));
    }

    @Test
    void setUserRole_changedUser() {
        project.setUserRole(newUser, ProjectRoleType.ADMIN, currentUser);

        project.setUserRole(newUser, ProjectRoleType.STAKEHOLDER, currentUser);

        assertThat(project.getProjectRoles()).contains(new ProjectRole(newUser, ProjectRoleType.STAKEHOLDER));
        assertThat(project.getRole(newUser)).contains(ProjectRoleType.STAKEHOLDER);
        assertThat(project.getHistoryEntries()).contains(new ProjectHistoryEntry(currentUser, now, ProjectHistoryType.USER_ROLE_CHANGED, newUser, ProjectRoleType.ADMIN.name(), ProjectRoleType.STAKEHOLDER.name()));
    }

    @Test
    void setUserRole_unchangedUser() {
        project.setUserRole(newUser, ProjectRoleType.ADMIN, currentUser);
        final var historySize = project.getHistoryEntries().size();

        project.setUserRole(newUser, ProjectRoleType.ADMIN, currentUser);

        assertThat(project.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void removeUserRole() {
        project.setUserRole(newUser, ProjectRoleType.ADMIN, currentUser);

        project.removeUserRole(newUser, currentUser);

        assertThat(project.getProjectRoles()).doesNotContain(new ProjectRole(newUser, ProjectRoleType.ADMIN));
        assertThat(project.getRole(newUser)).isEmpty();
        assertThat(project.getHistoryEntries()).contains(new ProjectHistoryEntry(currentUser, now, ProjectHistoryType.USER_REMOVED, newUser, ProjectRoleType.ADMIN.name(), null));
    }

    @Test
    void removeUserRole_unchangedUser() {
        project.removeUserRole(newUser, currentUser);
        final var historySize = project.getHistoryEntries().size();

        project.removeUserRole(newUser, currentUser);

        assertThat(project.getHistoryEntries()).hasSize(historySize);
    }
}